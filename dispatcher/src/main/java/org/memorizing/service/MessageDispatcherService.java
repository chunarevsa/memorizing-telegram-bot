package org.memorizing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.memorizing.entity.CardStockHistory;
import org.memorizing.entity.User;
import org.memorizing.entity.UserState;
import org.memorizing.model.EMode;
import org.memorizing.model.ERegularMessages;
import org.memorizing.model.command.ECommand;
import org.memorizing.model.command.EKeyboardCommand;
import org.memorizing.model.command.EPlaceholderCommand;
import org.memorizing.model.menu.EMenu;
import org.memorizing.model.menu.MenuFactory;
import org.memorizing.repository.UsersRepo;
import org.memorizing.resource.StorageResource;
import org.memorizing.resource.cardApi.*;
import org.springframework.stereotype.Service;

import java.net.ProtocolException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.memorizing.model.ERegularMessages.*;

@Service
public class MessageDispatcherService {
    private static final Logger log = Logger.getLogger(MessageDispatcherService.class);
    private final UsersRepo usersRepo;
    private final StorageResource storageResource;
    private final MenuService menuService;
    private final UserStateService userStateService;
    private final JsonObjectMapper mapper = new JsonObjectMapper();

    public MessageDispatcherService(
            UsersRepo usersRepo,
            StorageResource storageResource,
            MenuService menuService, UserStateService userStateService) {
        this.usersRepo = usersRepo;
        this.storageResource = storageResource;
        this.menuService = menuService;
        this.userStateService = userStateService;
    }

    // TODO: обработка ошибок, когда основной сервис не доступен сервис не доступен
    // TODO: не отображать start studyin если нет карточек
    // TODO: Отображать количество оставшихся карточек когда заходишь в обучение
    // TODO: отображать количество карточек в cardStock menu и cards

    public DispatcherResponse getResponseByCallback(Long chatId, String messageText) {
        log.debug("getResponseByCallback. req:" + chatId + ", " + messageText);
        User user = usersRepo.findByChatId(chatId);
        UserState userState = user.getUserState();
        MenuFactory menu = menuService.createMenuByCallback(user.getStorageId(), userState, userState.getCurrentMenu(), messageText);
        return new DispatcherResponse(menu, SUCCESSFULLY);
    }

    public DispatcherResponse getResponseByCommand(Long chatId, ECommand command) {
        log.debug("getResponseByCommand. req:" + chatId + ", " + command);
        User user = usersRepo.findByChatId(chatId);
        Integer storageId = user.getStorageId();
        UserState userState = user.getUserState();
        EMenu menuType = userState.getCurrentMenu();
        if (command == ECommand.START) menuType = EMenu.CARD_STOCKS;

        MenuFactory menu = menuService.createMenu(storageId, userState, menuType);
        // TODO: Add logic when something went wrong (need try catch in createMenu)
        return new DispatcherResponse(menu, SUCCESSFULLY);
    }

    public DispatcherResponse getResponseByPlaceholderCommand(Long chatId, String data) {
        log.debug("getResponseByPlaceholderCommand req:" + chatId + ", " + data);
        User user = usersRepo.findByChatId(chatId);
        Integer storageId = user.getStorageId();
        UserState userState = user.getUserState();
        EPlaceholderCommand command = EPlaceholderCommand.getPlaceholderCommandByPref(data);

        ERegularMessages status = executeRequest(command, data, userState);

        if (command == EPlaceholderCommand.DELETE_CARD_STOCK) {
            userState = userStateService.deleteCardStockIdFromSessionAndGet(userState);
        } else if (command == EPlaceholderCommand.DELETE_CARD) {
            userState = userStateService.deleteCardIdFromSessionAndGet(userState);
        }

        MenuFactory menu = menuService.createMenu(storageId, userState, userState.getLastMenu());
        return new DispatcherResponse(menu, status, true);
    }

    public DispatcherResponse getResponseByKeyboardCommand(Long chatId, EKeyboardCommand command) {
        log.debug("getResponseByKeyboardCommand req:" + chatId + ", " + command);
        User user = usersRepo.findByChatId(chatId);
        Integer storageId = user.getStorageId();
        UserState userState = user.getUserState();

        EMenu nextMenu = command.getNextMenu();
        DispatcherResponse resp = new DispatcherResponse();
        if (nextMenu == null) {
            switch (command) {
                case GET_INFO:
                    nextMenu = userState.getCurrentMenu();
                    break;
                case SKIP:
                    nextMenu = userState.getCurrentMenu();
                    resp = getResponseByNextButton(nextMenu, userState, true);
                    if (resp.getStatus() == COMPLETE_SET) nextMenu = nextMenu.getLastMenu();
                    break;
                case NEXT:
                    nextMenu = userState.getCurrentMenu();
                    resp = getResponseByNextButton(nextMenu, userState, false);
                    if (resp.getStatus() == COMPLETE_SET) nextMenu = nextMenu.getLastMenu();
                    break;
                case GO_BACK:
                    nextMenu = userState.getLastMenu();
                    break;
                case DELETE_CARD_STOCK:
                    return getResponseByPlaceholderCommand(chatId, "#delete-CardStock");
                case DELETE_CARD:
                    return getResponseByPlaceholderCommand(chatId, "#delete-Card");
            }
        }

        if (nextMenu != null) {
            resp.setMenu(menuService.createMenu(storageId, userState, nextMenu));
            return resp;
        } else return new DispatcherResponse(null, SOMETHING_WENT_WRONG);

    }

    private DispatcherResponse getResponseByNextButton(EMenu nextMenu, UserState userState, boolean isSkip) {
        EMode mode = EMode.getModeByMenu(nextMenu);

        DispatcherResponse resp = new DispatcherResponse();
        List<Integer> ids;

        Optional<CardStockHistory> cardStockHistory = userStateService.findCardStockHistoryByCardStockId(userState.getCardStockId());

        if (cardStockHistory.isEmpty() || cardStockHistory.get().getStudyingState().isEmpty()) {
            resp.setStatus(BAD_REQUEST);
            resp.setNeedSendStatus(true);
        } else {
            ids = userStateService.getCardIdsByHistory(cardStockHistory.get(), mode.name());

            if (isSkip)
                resp.setTestResult(storageResource.skipCard(userState.getCardStockId(), ids.get(0), mode.isFromKeyMode()));

            ids.remove(0);
            userStateService.updateHistoryByNewIds(cardStockHistory.get(), mode, ids);

            if (ids.isEmpty()) {
                // If it was last card
                resp.setStatus(COMPLETE_SET);
                resp.setNeedSendStatus(true);
            }
        }

        return resp;
    }

    public boolean isUserCurrentMenuStudying(Long chatId) {
        log.debug("isUserCurrentMenuStudying. req:" + chatId);
        EMenu currentMenu = usersRepo.findByChatId(chatId).getUserState().getCurrentMenu();
        return EMenu.getOnlyStudyingMenu().stream().anyMatch(it -> it == currentMenu);
    }

    public DispatcherResponse checkAnswer(Long chatId, String data) {
        log.debug("checkAnswer. req:" + chatId + ", " + data);
        User user = usersRepo.findByChatId(chatId);
        UserState userState = user.getUserState();

        EMenu nextMenu = userState.getCurrentMenu();
        EMode mode = EMode.getModeByMenu(nextMenu);

        Optional<CardStockHistory> history = userStateService.findCardStockHistoryByCardStockId(userState.getCardStockId());

        if (history.isEmpty()) {
            return new DispatcherResponse(menuService.createMenu(user.getStorageId(), userState, nextMenu), SOMETHING_WENT_WRONG, true);
        }

        List<Integer> ids = userStateService.getCardIdsByHistory(history.get(), mode.name());

        TestResultDto testResultDto = storageResource.checkCard(userState.getCardStockId(), ids.get(0), data, mode.isFromKeyMode());

        ids.remove(0);

        userStateService.updateHistoryByNewIds(history.get(), mode, ids);

        // If it was last card
        if (ids.isEmpty()) nextMenu = nextMenu.getLastMenu();

        return new DispatcherResponse(menuService.createMenu(user.getStorageId(), userState, nextMenu), SUCCESSFULLY, testResultDto);
    }

    public MenuFactory getFirstMenu(Long chatId) {
        log.debug("getFirstMenu. req:" + chatId);
        User user = usersRepo.findByChatId(chatId);
        UserState state = user.getUserState();

        // TODO: Temp. Clear it after adding auth
        EMenu firstMenu = EMenu.CARD_STOCKS;
        return menuService.createMenu(user.getStorageId(), state, firstMenu);
    }

    /**
     * Register in db through spring data
     *
     * @param chatId
     * @param userName
     */
    public void registerIfAbsent(Long chatId, String userName) {
        log.debug("registerIfAbsent. req:" + chatId + ", " + userName);
        if (!usersRepo.existsByChatId(chatId)) {
//            UserDto userDto = userResource.getUserByChatId(chatId);

            // TODO delete it after creating user-service
            Integer storageId;
            StorageDto storage = storageResource.getStorageByUserId(chatId);

            if (storage != null) {
                storageId = storage.getId();
            } else {
                storageId = storageResource.createStorage(new StorageFieldsDto(chatId, userName)).getId();
                addFirstData(storageId);
            }

            User user = new User(chatId, userName, storageId);
            usersRepo.save(user);
        }
    }

    private void addFirstData(Integer storageId) {
        CardStockFieldsDto firstReq = new CardStockFieldsDto(
                storageId,
                "English words",
                "All words I should remember",
                "ENG",
                "RUS",
                5,
                true,
                false
        );
        Integer firstCardStockId = storageResource.createCardStock(firstReq).getId();
        storageResource.createCard(
                new CardFieldsDto(firstCardStockId, "provide", "предоставлять", false)
        );
        storageResource.createCard(
                new CardFieldsDto(firstCardStockId, "memory", "память", false)
        );
        storageResource.createCard(
                new CardFieldsDto(firstCardStockId, "coffee", "кофе", false)
        );

        CardStockFieldsDto secondReq = new CardStockFieldsDto(
                storageId,
                "Interview",
                "Only provocation questions",
                "Question",
                "Answer",
                5,
                false,
                true
        );
        Integer secondCardStockId = storageResource.createCardStock(secondReq).getId();
        storageResource.createCard(
                new CardFieldsDto(
                        secondCardStockId,
                        "What three words do your co-workers use to describe you?",
                        "curious, scrupulous, conscientious",
                        true)
        );
        storageResource.createCard(
                new CardFieldsDto(
                        secondCardStockId,
                        "Do you prefer working alone or with a team or colleagues?",
                        "I like to work efficiently and it takes both to do that.",
                        true)
        );


        CardStockFieldsDto thirdReq = new CardStockFieldsDto(
                storageId,
                "IT terms",
                "Only IT terms in English",
                "ENG term",
                "translation",
                5,
                true,
                false
        );
        Integer thirdCardStockId = storageResource.createCardStock(thirdReq).getId();
        storageResource.createCard(
                new CardFieldsDto(thirdCardStockId, "instance", "экземпляр'", false)
        );
        storageResource.createCard(
                new CardFieldsDto(thirdCardStockId, "Inheritance", "наследование", false)
        );
        storageResource.createCard(
                new CardFieldsDto(thirdCardStockId, "return", "вернуть", false)
        );

    }

    private ERegularMessages executeRequest(EPlaceholderCommand command, String data, UserState userState) {
        log.debug("executeRequest. req:" + command + ", " + userState);

        IMappable entity = command.getNewEntity();
        if (!Objects.equals(command.getMethod(), "delete")) {

            try {
                String body = data.substring(data.indexOf("{"))
                        .replace("\n", "")
                        .replace("*", "")
                        .replace("`", "");

                entity = mapper.readValue(body, entity.getClass());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return BAD_REQUEST;
            } catch (ProtocolException e) {
                e.printStackTrace();
                return SOMETHING_WENT_WRONG;
            }
        }

        if (entity instanceof CardStockFieldsDto) {
            CardStockFieldsDto cardStockFieldsDto = (CardStockFieldsDto) entity;
            cardStockFieldsDto.setStorageId(userState.getUser().getStorageId());
            switch (command.getMethod()) {
                case "add":
                    storageResource.createCardStock(cardStockFieldsDto);
                    break;
                case "update":
                    storageResource.updateCardStock(cardStockFieldsDto, userState.getCardStockId());
                    break;
                case "delete":
                    List<CardDto> cards = storageResource.getCardsByCardStockId(userState.getCardStockId());

                    if (cards != null && !cards.isEmpty()) {
                        cards.forEach(cardDto -> storageResource.deleteCard(cardDto.getId()));
                    }

                    storageResource.deleteCardStock(userState.getCardStockId());
                    break;
                default:
                    return SOMETHING_WENT_WRONG;
            }

        } else if (entity instanceof CardFieldsDto) {
            CardFieldsDto cardFieldsDto = (CardFieldsDto) entity;
            cardFieldsDto.setCardStockId(userState.getCardStockId());
            CardStockDto cardStock = storageResource.getCardStockById(userState.getCardStockId());
            cardFieldsDto.setOnlyFromKey(cardStock.getOnlyFromKey());
            switch (command.getMethod()) {
                case "add":
                    storageResource.createCard(cardFieldsDto);
                    break;
                case "update":
                    cardFieldsDto.setCardKey(storageResource.getCardById(userState.getCardId()).getCardKey());
                    storageResource.updateCard(cardFieldsDto, userState.getCardId());
                    break;
                case "delete":
                    storageResource.deleteCard(userState.getCardId());
                    break;
                default:
                    return SOMETHING_WENT_WRONG;
            }

        } else {
            return SOMETHING_WENT_WRONG;
        }

        return SUCCESSFULLY;
    }

}

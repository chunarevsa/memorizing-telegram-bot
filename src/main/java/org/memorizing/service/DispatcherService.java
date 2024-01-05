package org.memorizing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.memorizing.entity.CardStockHistory;
import org.memorizing.entity.User;
import org.memorizing.entity.UserState;
import org.memorizing.model.EMode;
import org.memorizing.model.EStatus;
import org.memorizing.model.command.ECommand;
import org.memorizing.model.command.EKeyboardCommand;
import org.memorizing.model.command.EPlaceholderCommand;
import org.memorizing.model.menu.EMenu;
import org.memorizing.model.menu.Menu;
import org.memorizing.resource.StorageResource;
import org.memorizing.resource.cardApi.*;
import org.springframework.stereotype.Service;

import java.net.ProtocolException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.memorizing.model.EStatus.*;

@Service
public class DispatcherService {
    private static final Logger log = Logger.getLogger(DispatcherService.class);
    private final UserService userService;
    private final StorageResource storageResource;
    private final MenuService menuService;
    private final UserStateService userStateService;
    private final JsonObjectMapper mapper = new JsonObjectMapper();

    public DispatcherService(
            UserService userService,
            StorageResource storageResource,
            MenuService menuService, UserStateService userStateService) {
        this.userService = userService;
        this.storageResource = storageResource;
        this.menuService = menuService;
        this.userStateService = userStateService;
    }

    public DispatcherResponse getResponseByCallback(Long chatId, String data) {
        log.debug("getResponseByCallback. req:" + chatId + ", " + data);
        User user = userService.getByChatId(chatId);
        UserState userState = user.getUserState();
        Menu menu = menuService.createMenuByCallback(user.getStorageId(), userState, userState.getCurrentMenu(), data);
        return new DispatcherResponse(menu, SUCCESSFULLY);
    }

    public DispatcherResponse getResponseByCommand(Long chatId, ECommand command) {
        log.debug("getResponseByCommand. req:" + chatId + ", " + command);
        User user = userService.getByChatId(chatId);
        Integer storageId = user.getStorageId();
        UserState userState = user.getUserState();
        EMenu menuType = userState.getCurrentMenu();
        if (command == ECommand.START_COMMAND) menuType = EMenu.CARD_STOCKS;

        Menu menu = menuService.createMenu(storageId, userState, menuType);
        // TODO: Add logic when something went wrong (need try catch in createMenu)
        return new DispatcherResponse(menu, SUCCESSFULLY);
    }

    public DispatcherResponse getResponseByPlaceholderCommand(Long chatId, String data) {
        log.debug("getResponseByPlaceholderCommand req:" + chatId + ", " + data);
        User user = userService.getByChatId(chatId);
        Integer storageId = user.getStorageId();
        UserState userState = user.getUserState();
        EPlaceholderCommand command = EPlaceholderCommand.getPlaceholderCommandByPref(data);

        EStatus status = executeRequest(command, data, userState);
        EMenu menuType;

        if (command == EPlaceholderCommand.DELETE_CARD_STOCK) {
            userState = userStateService.deleteCardStockIdFromSessionAndGet(userState);
            menuType = userState.getLastMenu();
        } else if (command == EPlaceholderCommand.DELETE_CARD) {
            userState = userStateService.deleteCardIdFromSessionAndGet(userState, userState.getCardId());
            menuType = userState.getLastMenu();
        } else if (command == EPlaceholderCommand.ADD_CARD) {
            menuType = EMenu.CARDS;
        } else if (command == EPlaceholderCommand.ADD_CARD_STOCK) {
            menuType = EMenu.CARD_STOCKS;
        } else menuType = userState.getLastMenu();

        Menu menu = menuService.createMenu(storageId, userState, menuType);
        return new DispatcherResponse(menu, status, true);
    }

    public DispatcherResponse getResponseByKeyboardCommand(Long chatId, EKeyboardCommand command) {
        log.debug("getResponseByKeyboardCommand req:" + chatId + ", " + command);
        User user = userService.getByChatId(chatId);
        Integer storageId = user.getStorageId();
        UserState userState = user.getUserState();

        EMenu nextMenu = command.getNextMenu();
        DispatcherResponse resp = new DispatcherResponse();
        if (nextMenu == null) {
            switch (command) {
                case GET_INFO:
                    nextMenu = userState.getCurrentMenu();
                    break;
                case GO_BACK:
                    nextMenu = userState.getLastMenu();
                    break;
                case SKIP:
                case NEXT:
                    nextMenu = userState.getCurrentMenu();
                    resp = getResponseByNextButton(nextMenu, userState, command == EKeyboardCommand.SKIP);
                    userState = userStateService.findUserStateById(userState.getId()).orElse(userState);
                    if (resp.getStatus() == COMPLETE_SET) nextMenu = nextMenu.getLastMenu();
                    break;
                case GO_TO_CARD:
                    EMode mode = EMode.getModeByMenu(userState.getCurrentMenu());
                    Optional<CardStockHistory> cardStockHistory = userStateService.findCardStockHistoryByCardStockId(userState.getCardStockId());
                    if (cardStockHistory.isPresent()) {
                        List<Integer> ids = userStateService.getCardIdsByHistory(cardStockHistory.get(), mode.name());
                        if (!ids.isEmpty()) {
                            userState.setCardId(ids.get(0));
                            nextMenu = EMenu.CARD;
                        }
                    }

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
        } else return new DispatcherResponse(null, SOMETHING_WENT_WRONG); // TODO add throw Exception

    }

    private DispatcherResponse getResponseByNextButton(EMenu nextMenu, UserState userState, boolean isSkip) {
        log.debug("getResponseByNextButton req:" + nextMenu + ", " + isSkip);
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
        EMenu currentMenu = userService.getByChatId(chatId).getUserState().getCurrentMenu();
        return EMenu.getOnlyStudyingMenu().stream().anyMatch(it -> it == currentMenu);
    }

    public DispatcherResponse checkAnswer(Long chatId, String data) {
        log.debug("checkAnswer. req:" + chatId + ", " + data);
        User user = userService.getByChatId(chatId);
        UserState userState = user.getUserState();
        EMenu nextMenu = userState.getCurrentMenu();
        EMode mode = EMode.getModeByMenu(nextMenu);

        Optional<CardStockHistory> history = userStateService.findCardStockHistoryByCardStockId(userState.getCardStockId());

        if (history.isEmpty()) {
            log.debug("History for user:" + user.getId() + "is empty.");
            // TODO add throw Exception
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

    public Menu getFirstMenu(Long chatId) {
        log.debug("getFirstMenu. req:" + chatId);
        User user = userService.getByChatId(chatId);
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
        if (!userService.isUserExistsByChatId(chatId)) {
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
            userService.save(user);
        }
    }

    public DispatcherResponse getLastMenu(Long chatId) {
        log.debug("getLastMenu req:" + chatId);
        User user = userService.getByChatId(chatId);
        Integer storageId = user.getStorageId();
        UserState userState = user.getUserState();

        Menu menu = menuService.createMenu(storageId, userState, userState.getLastMenu());
        return new DispatcherResponse(menu, SUCCESSFULLY);
    }

    private EStatus executeRequest(EPlaceholderCommand command, String data, UserState userState) {
        log.debug("executeRequest. req:" + command + ", " + userState);

        IMappable entity = command.getNewEntity();
        String[] set = data.split(command.getPref());
        if (command.getMethod().equals("delete")) set = new String[]{"delete"};

        for (String element : set) {
            if(element.isBlank()) continue;
            if (!Objects.equals(element, "delete")) {
                try {
                    List<String> collect = element.lines().skip(1).map(line -> {
                        String field = line.replace("\n","").substring(0, line.indexOf(":"));
                        String value = line.substring(line.indexOf(":")+1).trim();
                        return '\"' + field + "\":\"" + value +'\"';
                    }).collect(Collectors.toList());

                    collect.set(0, '{' + collect.get(0));
                    collect.set(collect.size()-1, collect.get(collect.size()-1) + '}');

                    String temp = collect.toString();
                    String body = temp.substring(1, temp.length()-1);

                    entity = mapper.readValue(body, entity.getClass());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return BAD_REQUEST;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                    return SOMETHING_WENT_WRONG; // TODO add throw Exception
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
                        return SOMETHING_WENT_WRONG; // TODO add throw Exception
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
                        return SOMETHING_WENT_WRONG; // TODO add throw Exception
                }

            } else {
                // TODO add throw Exception
                return SOMETHING_WENT_WRONG;
            }
        }

        return SUCCESSFULLY;
    }

    // TODO: TEMP
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
}

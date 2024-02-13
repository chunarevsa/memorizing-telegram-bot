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
import org.memorizing.model.storage.Card;
import org.memorizing.model.storage.CardStock;
import org.memorizing.model.storage.Storage;
import org.memorizing.model.storage.TestResult;
import org.memorizing.resource.core.CardResource;
import org.memorizing.resource.core.CardStockResource;
import org.memorizing.resource.core.StorageResource;
import org.springframework.stereotype.Service;

import java.net.ProtocolException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.memorizing.model.EStatus.*;

@Service
public class DispatcherService {
    private static final Logger log = Logger.getLogger(DispatcherService.class);
    private final UserService userService;
    private final StorageResource storageResource;
    private final CardStockResource cardStockResource;
    private final CardResource cardResource;
    private final MenuService menuService;
    private final UserStateService userStateService;
    private final JsonObjectMapper mapper = new JsonObjectMapper();

    public DispatcherService(
            UserService userService,
            StorageResource storageResource,
            CardStockResource cardStockResource,
            CardResource cardResource,
            MenuService menuService,
            UserStateService userStateService) {
        this.userService = userService;
        this.storageResource = storageResource;
        this.cardStockResource = cardStockResource;
        this.cardResource = cardResource;
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
                resp.setTestResult(cardResource.skipCard(userState.getCardStockId(), ids.get(0), mode.isFromKeyMode()));

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
        TestResult testResult = cardResource.checkCard(userState.getCardStockId(), ids.get(0), data, mode.isFromKeyMode());
        ids.remove(0);
        userStateService.updateHistoryByNewIds(history.get(), mode, ids);

        // If it was last card
        if (ids.isEmpty()) nextMenu = nextMenu.getLastMenu();

        return new DispatcherResponse(menuService.createMenu(user.getStorageId(), userState, nextMenu), SUCCESSFULLY, testResult);
    }

    public Menu getFirstMenu(Long chatId) {
        log.debug("getFirstMenu. req:" + chatId);
        User user = userService.getByChatId(chatId);
        UserState state = user.getUserState();

        // TODO: Temp. Clear it after adding auth
        EMenu firstMenu = EMenu.CARD_STOCKS;
        return menuService.createMenu(user.getStorageId(), state, firstMenu);
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
        String[] elements = data.split(command.getPref());

        if (command.getMethod().equals("delete")) elements = new String[]{"delete"};
        for (String element : elements) {
            if (element.isBlank()) continue;
            if (!Objects.equals(element, "delete")) {
                try {
                    String[] fields = element.split("\n#");
                    StringBuilder body = new StringBuilder("{");
                    for (String field : fields) {
                        if (field.isBlank()) continue;
                        String fieldName = field.substring(0, field.indexOf(':')).trim();
                        String fieldValue = field.substring(field.indexOf(':') + 1).trim();
                        body.append('\"').append(fieldName).append("\":\"").append(fieldValue).append("\"");
                        body.append(field.equals(fields[fields.length - 1]) ? '}' : ',');
                    }

                    entity = mapper.readValue(body.toString(), entity.getClass());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return BAD_REQUEST;
                } catch (ProtocolException e) {
                    e.printStackTrace();
                    return SOMETHING_WENT_WRONG; // TODO add throw Exception
                }
            }

            if (entity instanceof CardStock) {
                CardStock cardStock = (CardStock) entity;
                cardStock.setStorageId(userState.getUser().getStorageId());
                switch (command.getMethod()) {
                    case "add":
                        cardStockResource.createCardStock(cardStock);
                        break;
                    case "update":
                        cardStockResource.updateCardStock(cardStock, userState.getCardStockId());
                        break;
                    case "delete":
                        List<Card> cards = cardResource.getCardsByCardStockId(userState.getCardStockId());

                        if (cards != null && !cards.isEmpty()) {
                            cards.forEach(cardDto -> cardResource.deleteCard(cardDto.getId()));
                        }

                        cardStockResource.deleteCardStock(userState.getCardStockId());
                        break;
                    default:
                        return SOMETHING_WENT_WRONG; // TODO add throw Exception
                }

            } else if (entity instanceof Card) {
                Card card = (Card) entity;
                card.setCardStockId(userState.getCardStockId());
                CardStock cardStock = cardStockResource.getCardStockById(userState.getCardStockId());

                switch (command.getMethod()) {
                    case "add":
                        cardResource.createCard(card, cardStock.getOnlyFromKey());
                        break;
                    case "update":
                        cardResource.updateCard(card, userState.getCardId());
                        break;
                    case "delete":
                        cardResource.deleteCard(userState.getCardId());
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

    // TODO: remove it, after creating auth-service and user-service
    @Deprecated
    public void createUserStorage(Long chatId, String username) throws Exception {
        Storage storage = new Storage();
        storage.setStorageName(username);
        storage.setUserId(chatId);

        Storage newStorage = storageResource.createStorage(storage);
        addFirstData(newStorage.getId());
        userService.addNew(chatId);
    }

    // TODO: create trigger in data base
    private void addFirstData(Integer storageId) {
        CardStock cardStock = new CardStock(
                null,
                storageId,
                "English words",
                "All words I should remember",
                "ENG",
                "RUS",
                5,
                true,
                false
        );
        Integer firstCardStockId = cardStockResource.createCardStock(cardStock).getId();

        cardResource.createCard(new Card(firstCardStockId, "provide", "предоставлять"), false);
        cardResource.createCard(new Card(firstCardStockId, "memory", "память"), false);
        cardResource.createCard(new Card(firstCardStockId, "coffee", "кофе"), false);

        CardStock secondCardStock = new CardStock(
                null,
                storageId,
                "Interview",
                "Only provocation questions",
                "Question",
                "Answer",
                5,
                false,
                true
        );
        Integer secondCardStockId = cardStockResource.createCardStock(secondCardStock).getId();

        cardResource.createCard(new Card(secondCardStockId,
                        "What three words do your co-workers use to describe you?",
                        "curious, scrupulous, conscientious"),
                true);
        cardResource.createCard(new Card(secondCardStockId,
                        "Do you prefer working alone or with a team or colleagues?",
                        "I like to work efficiently and it takes both to do that."),
                true);

        CardStock thirdCardStock = new CardStock(
                null,
                storageId,
                "IT terms",
                "Only IT terms in English",
                "ENG term",
                "translation",
                5,
                true,
                false
        );
        Integer thirdCardStockId = cardStockResource.createCardStock(thirdCardStock).getId();

        cardResource.createCard(
                new Card(thirdCardStockId, "instance", "экземпляр'"), false);
        cardResource.createCard(
                new Card(thirdCardStockId, "Inheritance", "наследование"), false);
        cardResource.createCard(
                new Card(thirdCardStockId, "return", "вернуть"), false);

    }
}

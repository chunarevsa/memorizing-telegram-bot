package org.memorizing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.memorizing.entity.User;
import org.memorizing.entity.UserState;
import org.memorizing.model.Constants;
import org.memorizing.model.menu.EMenu;
import org.memorizing.model.menu.MenuFactory;
import org.memorizing.repository.UsersRepo;
import org.memorizing.resource.StorageResource;
import org.memorizing.resource.UserResource;
import org.memorizing.resource.cardApi.*;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.net.ProtocolException;
import java.util.List;
import java.util.Optional;

import static org.memorizing.model.Constants.*;

@Service
public class MessageDispatcherService {
    private static final Logger log = Logger.getLogger(MessageDispatcherService.class);
    private final UsersRepo usersRepo;
    private final StorageResource storageResource;
    private final MenuService menuService;
    private final JsonObjectMapper mapper = new JsonObjectMapper();

    public MessageDispatcherService(
            UsersRepo usersRepo,
            UserResource userResource,
            StorageResource storageResource,
            MenuService menuService) {
        this.usersRepo = usersRepo;
        this.storageResource = storageResource;
        this.menuService = menuService;
    }

    public DispatcherResponse getResponseByRegularMessage(Long chatId, String messageText) throws Exception {
        log.debug("getResponseByRegularMessage. req:" + chatId + ", " + messageText);
        User user = usersRepo.findByChatId(chatId);
        Integer storageId = user.getStorageId();
        UserState userState = user.getUserState();
        MenuFactory menu = null;
        Constants status = null;

        switch (messageText) {
            case "info":
            case "/info":
                menu = menuService.createMenu(storageId, userState.getId(), userState.getCurrentMenu());
                break;
            case "back":
            case "/back":
                menu = menuService.createLastMenu(storageId, userState.getId(), userState.getLastMenu());
                break;
            case "add card stock":
                menu = menuService.createMenu(storageId, userState.getId(), EMenu.CARD_STOCK_ADD);
                break;
            case "update card stock":
                menu = menuService.createMenu(storageId, userState.getId(), EMenu.CARD_STOCK_UPDATE);
                break;
            case "delete card stock":
                return getResponseByPlaceholder(chatId, "#delete-CardStock");
            case "show cards":
                menu = menuService.createMenu(storageId, userState.getId(), EMenu.CARDS);
                break;
            case "add card":
                menu = menuService.createMenu(storageId, userState.getId(), EMenu.CARD_ADD);
                break;
            case "update card":
                menu = menuService.createMenu(storageId, userState.getId(), EMenu.CARD_UPDATE);
                break;
            case "delete card":
                return getResponseByPlaceholder(chatId, "#delete-Card");
            default:
                status = BAD_REQUEST;
                break;
        }

        if (menu == null && status == null) status = SOMETHING_WENT_WRONG;
        return new DispatcherResponse(menu, status);
    }

    /**
     * Callback always work after showing the question
     *
     * @param chatId
     * @param messageText
     * @return
     */

    public DispatcherResponse getResponseByCallback(Long chatId, String messageText) throws Exception {
        log.debug("getResponseByCallback. req:" + chatId + ", " + messageText);
        User user = usersRepo.findByChatId(chatId);
        UserState state = user.getUserState();
        MenuFactory menu = menuService.createMenuByCallback(user.getStorageId(), state.getId(), state.getCurrentMenu(), messageText);
        return new DispatcherResponse(menu, SUCCESSFULLY);
    }

    public DispatcherResponse getResponseByPlaceholder(Long chatId, String text) throws Exception {
        log.debug("getResponseByPlaceholder req:" + chatId + ", " + text);
        User user = usersRepo.findByChatId(chatId);
        Integer storageId = user.getStorageId();
        UserState userState = user.getUserState();
        Constants status;

        MenuFactory menu;

        if (text.isBlank() || !text.startsWith("#") || !text.lines().findFirst().get().contains("-")) {
            menu = menuService.createLastMenu(storageId, userState.getId(), userState.getLastMenu());
            return new DispatcherResponse(menu, BAD_REQUEST);
        }

        try {
            String[] methodAndEntity = text.lines().findFirst().get().substring(1).split("-");
            String method = methodAndEntity[0];
            String entity = methodAndEntity[1].trim();
            String body = null;

            if (!method.equals("add") && !method.equals("update") && !method.equals("delete")) {
                menu = menuService.createLastMenu(storageId, userState.getId(), userState.getLastMenu());
                return new DispatcherResponse(menu, BAD_REQUEST);
            }

            if (!method.equals("delete")) {
                body = text.substring(text.indexOf("{")).replace("\n", "");
            }

            status = executeRequest(entity, method, body, userState);
        } catch (Exception e) {
            e.printStackTrace();
            status = SOMETHING_WENT_WRONG;
        }

        menu = menuService.createLastMenu(storageId, userState.getId(), userState.getLastMenu());
        return new DispatcherResponse(menu, status);
    }

    /**
     * @param chatId
     * @param name
     * @return
     */
    public SendMessage getWelcomeMessage(Long chatId, String name) {
        log.debug("getWelcomeMessage. req:" + chatId);
        return SendMessage.builder()
                .chatId(chatId)
                .text(WELCOME_MESSAGE.toString().replaceAll("\\{name}", name))
                .build();
    }

    public MenuFactory getFirstMenu(Long chatId) throws Exception {
        log.debug("getFirstMenu. req:" + chatId);
        User user = usersRepo.findByChatId(chatId);
        UserState state = user.getUserState();

        // TODO: Temp. Clear it after adding auth
        EMenu firstMenu = EMenu.CARD_STOCKS;
        return menuService.createMenu(user.getStorageId(), state.getId(), firstMenu);
    }

    /**
     * Register in db through spring data
     *
     * @param chatId
     * @param userName
     */
    public void registerIfAbsent(Long chatId, String userName) {
        log.debug("getFirstMenu. req:" + chatId + ", " + userName);
        if (!usersRepo.existsByChatId(chatId)) {
//            UserDto userDto = userResource.getUserByChatId(chatId);

            // TODO delete it after creating user-service
            Integer storageId;
            StorageDto storage = storageResource.getStorageByUserId(Math.toIntExact(chatId));

            if (storage != null) {
                storageId = storage.getId();
            } else {
                storageId = storageResource.createStorage(new StorageFieldsDto(Math.toIntExact(chatId), userName)).getId();
            }

            User user = new User(chatId, userName, storageId);
            usersRepo.save(user);
        }
    }


    private Constants executeRequest(String entity, String method, String body, UserState userState) {
        log.debug("executeRequest. req:" + entity + ", " + method + ", " + body + ", " + userState);
        IMappable iMappable;

        if (entity.equals("CardStock")) {
            iMappable = new CardStockFieldsDto();
        } else if (entity.equals("Card")) {
            iMappable = new CardFieldsDto();
        } else return SOMETHING_WENT_WRONG;

        if (!method.equals("delete")) {
            try {
                iMappable = mapper.readValue(body, iMappable.getClass());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return BAD_REQUEST;
            } catch (ProtocolException e) {
                e.printStackTrace();
                return SOMETHING_WENT_WRONG;
            }
        }

        if (iMappable instanceof CardStockFieldsDto) {
            CardStockFieldsDto cardStockFieldsDto = (CardStockFieldsDto) iMappable;
            cardStockFieldsDto.setStorageId(userState.getUser().getStorageId());
            switch (method) {
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

        } else if (iMappable instanceof CardFieldsDto) {
            CardFieldsDto cardFieldsDto = (CardFieldsDto) iMappable;
            cardFieldsDto.setCardStockId(userState.getCardStockId());
            switch (method) {
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

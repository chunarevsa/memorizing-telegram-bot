package org.memorizing.service;

import org.apache.log4j.Logger;
import org.memorizing.model.menu.EMenu;
import org.memorizing.model.menu.MenuFactory;
import org.memorizing.resource.cardApi.CardDto;
import org.memorizing.resource.cardApi.UserDto;
import org.memorizing.entity.*;
import org.memorizing.resource.StorageResource;
import org.memorizing.resource.UserResource;
import org.memorizing.repository.UsersRepo;
import org.memorizing.resource.cardApi.CardStockDto;
import org.memorizing.resource.cardApi.StorageDto;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

import static org.memorizing.model.Constants.BAD_REQUEST;
import static org.memorizing.model.Constants.WELCOME_MESSAGE;

@Service
public class MessageDispatcherService {
    private static final Logger log = Logger.getLogger(MessageDispatcherService.class);

    private final UsersRepo usersRepo;
    private final UserResource userResource;
    // TODO delete it
    private final StorageResource storageResource;
    private final MenuService menuService;

    public MessageDispatcherService(UsersRepo usersRepo, UserResource userResource, StorageResource storageResource, MenuService menuService) {
        this.usersRepo = usersRepo;
        this.userResource = userResource;
        // TODO delete it
        this.storageResource = storageResource;
        this.menuService = menuService;
    }

    public MenuFactory getResponseByRegularMessage(Long chatId, String messageText) throws Exception {
        log.debug("getResponseByRegularMessage. req:" + chatId + ", " + messageText);
        User user = usersRepo.findByChatId(chatId);
        Integer storageId = user.getStorageId();
        UserState userState = user.getUserState();
        MenuFactory newMenu;

        switch (messageText) {
            case "info":
            case "/info":
                log.debug("Execute button INFO");
                newMenu = menuService.createMenu(storageId, userState.getId(), userState.getCurrentMenu());
                return newMenu;
            case "go to back":
            case "/goToBack":
                log.debug("Go to back");
                newMenu = menuService.createLastMenu(storageId, userState.getId(), userState.getLastMenu());
                break;
            case "add card stock":
            case "/addCardStock":
                log.debug("Execute button Add card stock");
                // TODO Temp
                newMenu = null;
                break;
            case "show cards":
                log.debug("Execute button Show cards");
                newMenu = menuService.createMenu(storageId, userState.getId(), EMenu.CARDS);
                break;
            default:
                log.debug("BAD BUTTON");
                return null;
        }
        return newMenu;
    }

    /**
     * Callback always work after showing the question
     *
     * @param chatId
     * @param messageText
     * @return
     */

    public MenuFactory getResponseByCallback(Long chatId, String messageText) throws Exception {
        log.debug("getResponseByCallback. req:" + chatId + ", " + messageText);
        User user = usersRepo.findByChatId(chatId);
        UserState state = user.getUserState();

        return menuService.createMenuByCallback(user.getStorageId(), state.getId(), state.getCurrentMenu(), messageText);
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
            UserDto userDto = userResource.getUserByChatId(chatId);
            StorageDto storageDto = storageResource.getStorageByUserId(userDto.getId());
            User user = new User(chatId, userName, storageDto.getId());
            usersRepo.save(user);
        }
    }

}

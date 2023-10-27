package org.memorizing.controller;

import org.apache.log4j.Logger;
import org.memorizing.model.menu.MenuFactory;
import org.memorizing.repository.UsersRepo;
import org.memorizing.resource.UserResource;
import org.memorizing.service.MessageDispatcherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.memorizing.model.Constants.BAD_REQUEST;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(TelegramBot.class);
    private final String botName;
    // TODO: create service
    private final UsersRepo usersRepo;
    private final UserResource userResource;
    private final MessageDispatcherService messageDispatcherService;
    // TODO: I think, it doesn't need
    public static ConcurrentHashMap<Long, LocalDate> users = new ConcurrentHashMap<>();
    // TODO: I think, it doesn't need
    public static ConcurrentHashMap<Long, Integer> usersWithChatIds = new ConcurrentHashMap<>();

    public TelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.name}") String botName,
            UsersRepo usersRepo,
            UserResource userResource,
            MessageDispatcherService messageDispatcherService) {
        super(botToken);
        this.botName = botName;
        this.usersRepo = usersRepo;
        this.userResource = userResource;
        this.messageDispatcherService = messageDispatcherService;
    }

    @PostConstruct
    void init() {
        userResource.getChatIdListWithUserId().forEach(user -> usersWithChatIds.put(user.getChatId(), user.getId()));
        log.debug("usersWithChatIds:" + usersWithChatIds.toString());
        usersRepo.findAll().forEach(user -> users.put(user.getChatId(), LocalDate.now()));
        log.debug("Users:" + users.toString());
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("onUpdateReceived:" + update + "\n---");

        Message message = null;
        String data = null;
        boolean hasCallback = false,
                hasRegularMessage = false;

        //take apart incoming update
        if (update.hasCallbackQuery()) {
            hasCallback = true;
            CallbackQuery callbackQuery = update.getCallbackQuery();
            message = callbackQuery.getMessage();
            data = callbackQuery.getData();
        } else if (update.hasMessage()) {
            hasRegularMessage = true;
            message = update.getMessage();
            data = message.getText();
        }

        if (hasCallback || hasRegularMessage) {
            Long chatId = message.getChatId();
            String userName = Optional.ofNullable(message.getFrom().getUserName())
                    .orElse(message.getFrom().getFirstName() + " " + message.getFrom().getLastName());

            MenuFactory menu;
            //routing by request
            if (hasCallback) {

                // TODO: Clear try catch
                try {
                    menu = messageDispatcherService.getResponseByCallback(chatId, data);
                    execute(getMessageWithTitleAndKeyboardByMenu(chatId, menu));
                    execute(getMessageWithTextAndInlineKeyboardByMenu(chatId, menu));

                } catch (Exception e) {
                    e.printStackTrace();

                }
            } else if (!users.containsKey(chatId) || data.equals("/start")) {
                // TODO: Add registration via browser
                // adding new user
                users.put(chatId, LocalDate.now());
                messageDispatcherService.registerIfAbsent(chatId, userName);
                try {
                    execute(messageDispatcherService.getWelcomeMessage(chatId, userName));
                    menu = messageDispatcherService.getFirstMenu(chatId);

                    execute(getMessageWithTitleAndKeyboardByMenu(chatId, menu));
                    execute(getMessageWithTextAndInlineKeyboardByMenu(chatId, menu));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    menu = messageDispatcherService.getResponseByRegularMessage(chatId, data);

                    if (menu == null) {
                        execute(SendMessage.builder()
                                .chatId(chatId)
                                .text(BAD_REQUEST.toString())
                                .build());
                    } else if (data.equals("info")) {
                        execute(SendMessage.builder()
                                .chatId(chatId)
                                .text(menu.getInfoText())
                                .build());
                    } else {
                        execute(getMessageWithTitleAndKeyboardByMenu(chatId, menu));
                        execute(getMessageWithTextAndInlineKeyboardByMenu(chatId, menu));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private SendMessage getMessageWithTitleAndKeyboardByMenu(Long chatId, MenuFactory menu) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(menu.getKeyboard())
                .text(menu.getName())
                .build();
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }

    private SendMessage getMessageWithTextAndInlineKeyboardByMenu(Long chatId, MenuFactory menu) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(menu.getInlineKeyboard())
                .text(menu.getText())
                .build();
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


}

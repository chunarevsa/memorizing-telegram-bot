package org.memorizing.botinstance;

import org.apache.log4j.Logger;
import org.memorizing.controller.MessageDispatcher;
import org.memorizing.repository.UserResource;
import org.memorizing.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(TelegramBot.class);
    private final String botName;
    private final UsersRepo usersRepo;
    private final UserResource userResource;
    private final MessageDispatcher messageDispatcher;
    public static ConcurrentHashMap<Long, LocalDate> users = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, Integer> usersWithChatIds = new ConcurrentHashMap<>();

    public TelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.name}") String botName,
            UsersRepo usersRepo,
            UserResource userResource,
            MessageDispatcher messageDispatcher) {
        super(botToken);
        this.botName = botName;
        this.usersRepo = usersRepo;
        this.userResource = userResource;
        this.messageDispatcher = messageDispatcher;
    }

    /**
     * -- Card Stocks menu (1):
     * Список Card Stock
     * "ENG/RUS" "Interview question/Answer"          -> Card stock menu
     * <p>
     * "add card stock"                               -> adding Card stock menu
     * "info"                                         -> info menu 0
     * <p>
     * -- Card stock menu (2)
     * Информация о текущем Card Stock   / ENG/RUS
     * Какие доступны режимы                          / TESTING_TO_KEY, TESTING_FROM_KEY, SHOWING SELF-CHECK
     * Сколько карточек                               / 385
     * Показывается статистика по доступным режимам   / FROM_KEY HARD:20 NORMAL:60 COMPLETED 305
     * / TO_KEY HARD:15 NORMAL:200 COMPLETED 170
     * <p>
     * "start studying"                               -> Studying menu
     * "show cards"                                   -> Cards menu
     * "edit card stock"                              -> editing Card Stock menu
     * "delete card stock"                            -> Delete Card stock and -> Main menu (1)
     * "info"                                         -> info menu 2
     * "go to back"                                   -> Card Stocks (1)
     * <p>
     * -- Cards menu (3)
     * Все карточки из этого Card Stock
     * <p>
     * "add card"                                     -> adding Card menu
     * "go to back"                                   -> Card stock menu (2)
     * "info"                                         -> info menu 3
     * <p>
     * -- Card menu (4)
     * Информация о карте
     * <p>
     * "edit card"                                    -> editing Card menu
     * "delete card"                                  -> Delete Card and -> Cards menu (3)
     * "info"                                         -> info menu 2
     * "go to back"                                   -> Card Stocks (1)
     */


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

            //routing by request
            if (hasCallback) {
                SendMessage responseByCallback = messageDispatcher.getResponseByCallback(chatId, data);
                try {
                    execute(responseByCallback);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                    try {
                        responseByCallback.setParseMode("HTML");
                        responseByCallback.setText("<pre>" + responseByCallback.getText() + "</pre>");
                        execute(responseByCallback);
                    } catch (TelegramApiException ee) {
                        responseByCallback.enableMarkdown(false);
                        try {
                            execute(responseByCallback);
                        } catch (TelegramApiException ex) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (!users.containsKey(chatId)) {
                // TODO: Add registration via browser
                // adding new user
                users.put(chatId, LocalDate.now());
                messageDispatcher.registerIfAbsent(chatId, userName);
                try {
                    execute(messageDispatcher.getWelcomeMessage(chatId, userName));

                    execute(messageDispatcher.getMessageForMainMenu(chatId, data));



                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
//            } else if (!usersWithChatIds.containsKey(chatId)) {
//                usersWithChatIds.put(chatId, userId);
//                users.put(chatId, LocalDate.now());
//                messageDispatcher.registerIfAbsent(chatId, userName);
//                try {
//                    execute(messageDispatcher.getWelcomeMessage(chatId, userName));
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
            } else {
                try {
                    execute(messageDispatcher.getResponseByRegularMessage(chatId, data));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

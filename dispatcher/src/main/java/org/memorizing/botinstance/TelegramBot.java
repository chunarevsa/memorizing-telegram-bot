package org.memorizing.botinstance;

import org.apache.log4j.Logger;
import org.memorizing.controller.MessageDispatcher;
import org.memorizing.repository.StorageResource;
import org.memorizing.repository.UserResource;
import org.memorizing.repository.UsersRepo;
import org.memorizing.utils.cardApi.StorageDto;
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
    private final StorageResource storageResource;
    private final UserResource userResource;
    private final MessageDispatcher messageDispatcher;
    public static ConcurrentHashMap<Long, LocalDate> users = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, Integer> usersWithChatIds = new ConcurrentHashMap<>();

    public TelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.name}") String botName,
            UsersRepo usersRepo,
            StorageResource storageResource,
            UserResource userResource,
            MessageDispatcher messageDispatcher) {
        super(botToken);
        this.botName = botName;
        this.usersRepo = usersRepo;
        this.storageResource = storageResource;
        this.userResource = userResource;
        this.messageDispatcher = messageDispatcher;
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
        Long chatId = update.getMessage().getChatId();

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
            String userName = Optional.ofNullable(message.getFrom().getUserName())
                    .orElse(message.getFrom().getFirstName() + " " + message.getFrom().getLastName());

            //routing by request
            if (hasCallback) {
                SendMessage responseByCallback = messageDispatcher.getResponseByCallback(chatId, data);
                try {
                    execute(responseByCallback);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
//                    try {
//                        responseByCallback.setParseMode("HTML");
//                        responseByCallback.setText("<pre>" + responseByCallback.getText() + "</pre>");
//                        execute(responseByCallback);
//                    } catch (TelegramApiException ee) {
//                        responseByCallback.enableMarkdown(false);
//                        execute(responseByCallback);
//                    }
                }
            } else if (!users.containsKey(chatId)) {
                users.put(chatId, LocalDate.now());
                messageDispatcher.registerIfAbsent(chatId, userName);
                try {
                    execute(messageDispatcher.getWelcomeMessage(chatId, userName));
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

package org.memorizing.controller;

import org.apache.log4j.Logger;
import org.memorizing.model.Constants;
import org.memorizing.model.menu.MenuFactory;
import org.memorizing.repository.UsersRepo;
import org.memorizing.resource.UserResource;
import org.memorizing.service.DispatcherResponse;
import org.memorizing.service.MessageDispatcherService;
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

import static org.memorizing.model.Constants.BAD_REQUEST;
import static org.memorizing.model.Constants.SUCCESSFULLY;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(TelegramBot.class);
    private final String botName;
    private final UsersRepo usersRepo;
    private final MessageDispatcherService messageDispatcherService;
    public static ConcurrentHashMap<Long, LocalDate> users = new ConcurrentHashMap<>();

    public TelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.name}") String botName,
            UsersRepo usersRepo,
            UserResource userResource,
            MessageDispatcherService messageDispatcherService) {
        super(botToken);
        this.botName = botName;
        this.usersRepo = usersRepo;
        this.messageDispatcherService = messageDispatcherService;
    }

    @PostConstruct
    void init() {
        // TODO: add finding user in user-service, when it will be completed
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
            try {
                if (hasCallback) {
                    DispatcherResponse resp = messageDispatcherService.getResponseByCallback(chatId, data);
                    executeSending(chatId, resp.getMenu(), resp.getStatus());

                } else if (!users.containsKey(chatId) || data.equals("/start")) {
                    // TODO: Add registration via auth-service, when it will be completed
                    // adding new user
                    messageDispatcherService.registerIfAbsent(chatId, userName);
                    users.put(chatId, LocalDate.now());

                    execute(messageDispatcherService.getWelcomeMessage(chatId, userName));
                    menu = messageDispatcherService.getFirstMenu(chatId);
                    executeSending(chatId, menu, SUCCESSFULLY);
                } else if (data.startsWith("#")) {
                    // TODO: for test
                    if(data.startsWith("#test")) {
                        startEcho(chatId, data);
                        return;
                    }

                    DispatcherResponse resp = messageDispatcherService.getResponseByPlaceholder(chatId, data);
                    // send status
                    execute(SendMessage.builder()
                            .chatId(chatId)
                            .text(resp.getStatus().toString())
                            .build());
                    // back to last menu
                    executeSending(chatId, resp.getMenu(), resp.getStatus());
                } else {
                    DispatcherResponse resp = messageDispatcherService.getResponseByRegularMessage(chatId, data);

                    if (data.equals("info") && resp.getMenu() != null) {
                        SendMessage infoText = SendMessage.builder()
                                .chatId(chatId)
                                .text(resp.getMenu().getInfoText())
                                .build();
                        infoText.enableMarkdown(true);
                        // send only info text
                        execute(infoText);
                    } else executeSending(chatId, resp.getMenu(), resp.getStatus());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void executeSending(Long chatId, MenuFactory menu, Constants status) throws TelegramApiException {
        if (status != null && status != SUCCESSFULLY) {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(status.toString())
                    .build());
        }

        if (menu == null) {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(BAD_REQUEST.toString())
                    .build());
            return;
        }

        SendMessage messageWithKeyboard = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(menu.getKeyboard())
                .text(menu.getTitle())
                .build();
        messageWithKeyboard.enableMarkdown(true);
        execute(messageWithKeyboard);

        SendMessage messageWithInlineKeyboard = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(menu.getInlineKeyboard())
                .text(menu.getText())
                .build();
        messageWithInlineKeyboard.enableMarkdown(true);
        execute(messageWithInlineKeyboard);
    }

    private void startEcho(Long chatId, String data) throws TelegramApiException {
        String testString = data.substring("#test ".length());
        log.debug("testString: "+ testString);

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(null)
                .text("Markdown: " + testString)
                .build();
        message.enableMarkdown(true);
        execute(message);

        String innerText = "|innerText|";
        message = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(null)
                .text("Markdown" + innerText)
                .build();
        message.enableMarkdown(true);
        execute(message);

        String innerText2 = "||innerText||";
        message = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(null)
                .text("MarkdownV2: " + innerText2)
                .build();
        message.enableMarkdownV2(true);
        execute(message);

    }

}

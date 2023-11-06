package org.memorizing.controller;

import org.apache.log4j.Logger;
import org.memorizing.model.ERegularMessages;
import org.memorizing.model.command.ECommand;
import org.memorizing.model.command.EKeyboardCommand;
import org.memorizing.model.command.EPlaceholderCommand;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.memorizing.model.ERegularMessages.*;

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
                    executeSendingNew(chatId, resp);

                } else if (!users.containsKey(chatId)) {
                    // TODO: Add registration via auth-service, when it will be completed
                    // adding new user
                    messageDispatcherService.registerIfAbsent(chatId, userName);
                    users.put(chatId, LocalDate.now());

                    // Send welcome message
                    SendMessage welcomeMessage = SendMessage.builder()
                            .chatId(chatId)
                            .text((WELCOME.getText() + HOW_IT_WORKS.getText()).replaceAll("\\{name}", userName))
                            .build();
                    welcomeMessage.enableMarkdown(true);
                    execute(welcomeMessage);

                    menu = messageDispatcherService.getFirstMenu(chatId);
                    executeSendingNew(chatId, new DispatcherResponse(menu, SUCCESSFULLY));

                } else if (ECommand.getCommandByMessage(data) != null) {
                    // обработка команд из основной менюшки
                    ECommand command = ECommand.getCommandByMessage(data);

                    SendMessage commandMessage = SendMessage.builder()
                            .chatId(chatId)
                            .text(command.getMessageText().replaceAll("\\{name}", userName))
                            .build();
                    commandMessage.enableMarkdown(true);

                    execute(commandMessage);
                    executeSendingNew(chatId, messageDispatcherService.getResponseByCommand(chatId, command));

                } else if (EPlaceholderCommand.getPlaceholderCommandByPref(data) != null) {
                    // Обработка входящего изменения

                    DispatcherResponse resp = messageDispatcherService.getResponseByPlaceholderCommand(chatId, data);
                    executeSendingNew(chatId, resp);

                } else if (EKeyboardCommand.getKeyboardCommandByMessage(data) != null) {
                    // обработка с основной клавиатуры
                    EKeyboardCommand command = EKeyboardCommand.getKeyboardCommandByMessage(data);

                    DispatcherResponse resp = messageDispatcherService.getResponseByKeyboardCommand(chatId, command);
                    if (command == EKeyboardCommand.GET_INFO) {
                        SendMessage infoText = SendMessage.builder()
                                .chatId(chatId)
                                .text(resp.getMenu().getInfoText())
                                .build();
                        infoText.enableMarkdown(true);
                        execute(infoText);
                    }
                    executeSendingNew(chatId, resp);

                } else if (messageDispatcherService.isUserCurrentMenuStudying(chatId)) {
                    // возможно это ответ на тест
                    log.debug("Test");

                } else {
                    executeSendingNew(chatId, new DispatcherResponse(null, BAD_REQUEST, true));
                }

//                else if (data.startsWith("#")) {
//
//                    DispatcherResponse resp = messageDispatcherService.getResponseByPlaceholder(chatId, data);
//                    if (resp.getStatus() == SUCCESSFULLY) {
//                        execute(SendMessage.builder()
//                                .chatId(chatId)
//                                .text(SUCCESSFULLY.toString())
//                                .build());
//                    }
//
//                    executeSending(chatId, resp.getMenu(), resp.getStatus());
//                } else if (messageDispatcherService.isUserCurrentMenuStudying(chatId)) {
//                    // skip
//                    // обеспечивать
//                    // info
//                    // back
//                    //
//
//                    DispatcherResponse resp = messageDispatcherService.getResponseByStudyingMenu();
//
//
//                } else {
//                    DispatcherResponse resp = messageDispatcherService.getResponseByRegularMessage(chatId, data);
//
//                    if ((data.equals("info") || data.equals("/info")) && resp.getMenu() != null) {
//                        SendMessage infoText = SendMessage.builder()
//                                .chatId(chatId)
//                                .text(resp.getMenu().getInfoText())
//                                .build();
//                        infoText.enableMarkdown(true);
//                        // send only info text
//                        execute(infoText);
//                        return;
//                    } else if ((data.equals("howitworks") || data.equals("/howitworks")) && resp.getMenu() != null) {
//                        SendMessage helpText = SendMessage.builder()
//                                .chatId(chatId)
//                                .text(HOW_IT_WORKS.toString())
//                                .build();
//                        helpText.enableMarkdown(true);
//                        // Send help text and current menu
//                        execute(helpText);
//                    } else if ((data.equals("help") || data.equals("/help")) && resp.getMenu() != null) {
//                        SendMessage helpText = SendMessage.builder()
//                                .chatId(chatId)
//                                .text(HELP.toString())
//                                .build();
//                        helpText.enableMarkdown(true);
//                        // Send help text and current menu
//                        execute(helpText);
//                    }
//
//                    executeSending(chatId, resp.getMenu(), resp.getStatus());
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private void executeSending(Long chatId, MenuFactory menu, RegularMessages status) throws TelegramApiException {
//        if (status != null && status != SUCCESSFULLY) {
//            execute(SendMessage.builder()
//                    .chatId(chatId)
//                    .text(status.toString())
//                    .build());
//        }
//
//        if (menu == null) {
//            execute(SendMessage.builder()
//                    .chatId(chatId)
//                    .text(BAD_REQUEST.toString())
//                    .build());
//            return;
//        }
//
//        SendMessage messageWithKeyboard = SendMessage.builder()
//                .chatId(chatId)
//                .replyMarkup(menu.getKeyboard())
//                .text(menu.getTitle())
//                .build();
//        messageWithKeyboard.enableMarkdown(true);
//        execute(messageWithKeyboard);
//
//        SendMessage messageWithInlineKeyboard = SendMessage.builder()
//                .chatId(chatId)
//                .replyMarkup(menu.getInlineKeyboard())
//                .text(menu.getText())
//                .build();
//        messageWithInlineKeyboard.enableMarkdown(true);
//        execute(messageWithInlineKeyboard);
//    }

    private void executeSendingNew(Long chatId, DispatcherResponse resp) throws TelegramApiException {
        ERegularMessages status = resp.getStatus();
        MenuFactory menu = resp.getMenu();

        if (resp.isNeedSendStatus()) {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(status.getText())
                    .build());
        }

        if (menu == null) return;

        if (resp.isNeedSendTitle()) {
            SendMessage messageWithKeyboard = SendMessage.builder()
                    .chatId(chatId)
                    .replyMarkup(menu.getKeyboard())
                    .text(menu.getTitle())
                    .build();
            messageWithKeyboard.enableMarkdown(true);
            execute(messageWithKeyboard);
        }

        SendMessage messageWithInlineKeyboard = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(menu.getInlineKeyboard())
                .text(menu.getText())
                .build();
        messageWithInlineKeyboard.enableMarkdown(true);
        execute(messageWithInlineKeyboard);
    }


    // For testing "Styled" text
    private void startEcho(Long chatId, String data) throws TelegramApiException {
        String testString = data.substring("#test ".length());
        log.debug("testString: " + testString);
        String tempVar = "\\uD83D\uDD18▪️▫️\uD83D\uDD39✅❗️❌\uD83D\uDC49\uD83E\uDEE5";

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(null)
                .text(testString)
                .build();
        execute(sendMessage);

        List<String> messages = new ArrayList<>();
        messages.add("Some text with Styled text");

        messages.forEach(message -> {
            try {
                sendMessage.setText(message + " MD");
                sendMessage.enableMarkdownV2(false);
                sendMessage.enableMarkdown(true);
                execute(sendMessage);
            } catch (Exception e) {
                log.debug("exception MD with message:" + message);
            }

            try {
                sendMessage.setText(message + " MDV2");
                sendMessage.enableMarkdown(false);
                sendMessage.enableMarkdownV2(true);
                execute(sendMessage);
            } catch (Exception e) {
                log.debug("exception MDV2 with message:" + message);
            }

        });

        List<String> trueMDList = new ArrayList<>();
        trueMDList.add("`Inner text`");
        trueMDList.add("*innerText*");
        trueMDList.add("#Inner text");
        trueMDList.add("_Inner text_");
        trueMDList.forEach(message -> {
            try {
                sendMessage.setText(message + " MD");
                sendMessage.enableMarkdownV2(false);
                sendMessage.enableMarkdown(true);
                execute(sendMessage);
            } catch (Exception e) {
                log.debug("exception MD with message:" + message);
            }
        });

        List<String> trueMDV2List = new ArrayList<>();
        trueMDV2List.add(">innerText ");
        trueMDV2List.add("* innerText *");
        trueMDV2List.add("_Inner text_");
        trueMDV2List.add("~Inner text~");
        trueMDV2List.add("__Inner text__");
        trueMDV2List.forEach(message -> {
            try {
                sendMessage.setText(message + " MDV2");
                sendMessage.enableMarkdown(false);
                sendMessage.enableMarkdownV2(true);
                execute(sendMessage);
            } catch (Exception e) {
                log.debug("exception MDV2 with message:" + message);
            }
        });
    }
}

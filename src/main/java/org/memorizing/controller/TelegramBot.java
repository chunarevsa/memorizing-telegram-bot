package org.memorizing.controller;

import org.apache.log4j.Logger;
import org.memorizing.model.command.ECommand;
import org.memorizing.model.command.EKeyboardCommand;
import org.memorizing.model.command.EPlaceholderCommand;
import org.memorizing.model.menu.AStudyingMenu;
import org.memorizing.model.menu.MenuFactory;
import org.memorizing.model.menu.SelfCheckMenu;
import org.memorizing.resource.cardApi.CardDto;
import org.memorizing.resource.cardApi.TestResultDto;
import org.memorizing.service.DispatcherResponse;
import org.memorizing.service.MessageDispatcherService;
import org.memorizing.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.memorizing.model.ERegularMessages.HOW_IT_WORKS;
import static org.memorizing.model.ERegularMessages.WELCOME;
import static org.memorizing.model.EStatus.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(TelegramBot.class);
    private final String botName;
    private final UserService userService;
    private final MessageDispatcherService messageDispatcherService;

    public TelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.name}") String botName,
            UserService userService,
            MessageDispatcherService messageDispatcherService) {
        super(botToken);
        this.botName = botName;
        this.userService = userService;
        this.messageDispatcherService = messageDispatcherService;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message = null;
        String text = null;
        String data = null;
        boolean hasCallback = false,
                hasRegularMessage = false;

        //take apart incoming update
        if (update.hasCallbackQuery()) {
            hasCallback = true;
            CallbackQuery callbackQuery = update.getCallbackQuery();
            message = callbackQuery.getMessage();
            data = callbackQuery.getData();
            text = callbackQuery.getData();
        } else if (update.hasMessage()) {
            hasRegularMessage = true;
            message = update.getMessage();
            text = message.getText();
        }

        log.info("onUpdateReceived:" + text + "\n---");

        if (hasCallback || hasRegularMessage) {
            Long chatId = message.getChatId();
            String userName = Optional.ofNullable(message.getFrom().getUserName())
                    .orElse(message.getFrom().getFirstName() + " " + message.getFrom().getLastName());

            try {
                if (hasCallback) {
                    DispatcherResponse resp = messageDispatcherService.getResponseByCallback(chatId, data);
                    sendMenu(chatId, resp.getMenu());

                } else if (!userService.isUserExistsByChatId(chatId)) {
                    // TODO: Add registration via auth-service, when it will be completed
                    // adding new user
                    messageDispatcherService.registerIfAbsent(chatId, userName);
                    String welcomeMessage = (WELCOME.getText() + HOW_IT_WORKS.getText()).replaceAll("\\{name}", userName);

                    executeSendingMessage(chatId, welcomeMessage);
                    sendMenu(chatId, messageDispatcherService.getFirstMenu(chatId));

                } else if (ECommand.getCommandByMessage(text) != null) {
                    // обработка команд из основной менюшки
                    ECommand command = ECommand.getCommandByMessage(text);

                    executeSendingMessage(chatId, command.getMessageText().replaceAll("\\{name}", userName));
                    sendMenu(chatId, messageDispatcherService.getResponseByCommand(chatId, command).getMenu());

                } else if (EPlaceholderCommand.getPlaceholderCommandByPref(text) != null) {
                    // Обработка входящего изменения
                    DispatcherResponse resp = messageDispatcherService.getResponseByPlaceholderCommand(chatId, text);

                    executeSendingMessage(chatId, resp.getStatus().getText());
                    sendMenu(chatId, resp.getMenu());

                } else if (EKeyboardCommand.getKeyboardCommandByMessage(text) != null) {
                    // обработка с основной клавиатуры
                    EKeyboardCommand command = EKeyboardCommand.getKeyboardCommandByMessage(text);

                    DispatcherResponse resp = messageDispatcherService.getResponseByKeyboardCommand(chatId, command);
                    if (command == EKeyboardCommand.GET_INFO) executeSendingMessage(chatId, resp.getMenu().getInfoText());

                    if (resp.getMenu() instanceof SelfCheckMenu) {

                        executeSendingMenu(chatId, resp.getMenu(), command != EKeyboardCommand.NEXT,true);
                        if (resp.isNeedSendStatus()) executeSendingMessage(chatId, resp.getStatus().getText());

                    } else if (command == EKeyboardCommand.NEXT && resp.getMenu() instanceof AStudyingMenu) {

                        executeSendingMenu(chatId, resp.getMenu(), false, false);
                        if (resp.isNeedSendStatus()) executeSendingMessage(chatId, resp.getStatus().getText());

                    } else if (command == EKeyboardCommand.NEXT && !(resp.getMenu() instanceof AStudyingMenu)) {

                        executeSendingMessage(chatId, resp.getStatus().getText());
                        sendMenu(chatId, resp.getMenu());

                    } else if (command == EKeyboardCommand.SKIP) {
                        sendCorrectAnswer(chatId, resp.getMenu(), resp.getTestResult());

                    } else sendMenu(chatId, resp.getMenu());

                } else if (messageDispatcherService.isUserCurrentMenuStudying(chatId)) {
                    // may be, it is user test answer
                    DispatcherResponse resp = messageDispatcherService.checkAnswer(chatId, text);

                    if (!resp.getTestResult().getRightAnswer()) sendCorrectAnswer(chatId, resp.getMenu(), resp.getTestResult());

                    // If we continue testing
                    if (resp.getMenu() instanceof AStudyingMenu) {
                        executeSendingMenu(chatId, resp.getMenu(), false, false);
                    } else {
                        executeSendingMessage(chatId, COMPLETE_SET.getText());
                        executeSendingMenu(chatId, resp.getMenu(), true, false);
                    }

                } else {
                    executeSendingMessage(chatId, BAD_REQUEST.getText());
                    DispatcherResponse resp = messageDispatcherService.getLastMenu(chatId);
                    sendMenu(chatId, resp.getMenu());
                }

            } catch (Exception e) {
                executeSendingMessage(chatId, SOMETHING_WENT_WRONG.getText());
                DispatcherResponse resp = messageDispatcherService.getLastMenu(chatId);
                sendMenu(chatId, resp.getMenu());
                e.printStackTrace();
            }
        }
    }

    private void sendMenu(Long chatId, MenuFactory menu) {
        executeSendingMenu(chatId, menu, true, false);
    }

    private void executeSendingMenu(Long chatId, MenuFactory menu, Boolean needSendTitle, Boolean enableMDV2) {
        try {
            if (needSendTitle) {
                SendMessage titleMessage = SendMessage.builder()
                        .chatId(chatId)
                        .replyMarkup(menu.getKeyboard())
                        .text(menu.getTitle())
                        .build();
                titleMessage.enableMarkdown(true);
                execute(titleMessage);
            }

            SendMessage textMessage = SendMessage.builder()
                    .chatId(chatId)
                    .replyMarkup(needSendTitle ? menu.getInlineKeyboard() : menu.getKeyboard())
                    .text(menu.getText())
                    .build();

            if (enableMDV2) {
                String str = menu.getText()
                        .replaceAll("-", ":")
                        .replaceAll("\\(", "[")
                        .replaceAll("\\)", "]")
                        .replaceAll("\\.", ";");
                textMessage.setText(str);

                textMessage.enableMarkdownV2(true);
            } else textMessage.enableMarkdown(true);

            execute(textMessage);

        } catch (TelegramApiException e) {
            executeSendingMessage(chatId, SOMETHING_WENT_WRONG.getText());
            DispatcherResponse resp = messageDispatcherService.getLastMenu(chatId);
            sendMenu(chatId, resp.getMenu());
            e.printStackTrace();
        }

    }

    private void executeSendingMessage(Long chatId, String text) {
        try {
            SendMessage messageWithKeyboard = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
            messageWithKeyboard.enableMarkdown(true);
            execute(messageWithKeyboard);
        } catch (TelegramApiException e) {
            executeSendingMessage(chatId, SOMETHING_WENT_WRONG.getText());
            DispatcherResponse resp = messageDispatcherService.getLastMenu(chatId);
            sendMenu(chatId, resp.getMenu());
            e.printStackTrace();
        }
    }

    private void sendCorrectAnswer(Long chatId, MenuFactory menu, TestResultDto result) {
        String correctAnswer;
        CardDto card = result.getCard();

        // We can't show last card in a correct queue because we have only next menu
        if (menu instanceof AStudyingMenu) {
            AStudyingMenu studyingMenu = (AStudyingMenu) menu;
            correctAnswer = studyingMenu.getMode().isFromKeyMode()
                    ? card.getCardKey() + " : " + card.getCardValue()
                    : card.getCardValue() + " : " + card.getCardKey();

        } else correctAnswer = card.getCardKey() + " : " + card.getCardValue();

        executeSendingMessage(chatId, "❗" + correctAnswer + "❗");
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

        trueMDList.add("|Inner text|");
        trueMDList.add("||Inner text||");
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

        trueMDV2List.add("||Inner text||");
        trueMDV2List.add("|Inner text|");
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

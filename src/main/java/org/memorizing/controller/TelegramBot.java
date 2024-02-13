package org.memorizing.controller;

import org.apache.log4j.Logger;
import org.memorizing.model.command.ECommand;
import org.memorizing.model.command.EKeyboardCommand;
import org.memorizing.model.command.EPlaceholderCommand;
import org.memorizing.model.menu.AStudyingMenu;
import org.memorizing.model.menu.Menu;
import org.memorizing.model.menu.SelfCheckMenu;
import org.memorizing.model.storage.Card;
import org.memorizing.model.storage.TestResult;
import org.memorizing.service.DispatcherResponse;
import org.memorizing.service.DispatcherService;
import org.memorizing.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

import static org.memorizing.model.ERegularMessages.HOW_IT_WORKS;
import static org.memorizing.model.ERegularMessages.WELCOME;
import static org.memorizing.model.EStatus.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(TelegramBot.class);
    private final String botName;
    private final UserService userService;
    private final DispatcherService dispatcherService;

    public TelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.name}") String botName,
            UserService userService,
            DispatcherService dispatcherService) {
        super(botToken);
        this.botName = botName;
        this.userService = userService;
        this.dispatcherService = dispatcherService;
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
        boolean hasCallback = false;
        boolean hasRegularMessage = false;

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

        log.info("onUpdateReceived:" + text);

        if (hasCallback || hasRegularMessage) {
            Long chatId = message.getChatId();
            String userName = Optional.ofNullable(message.getFrom().getUserName())
                    .orElse(message.getFrom().getFirstName() + " " + message.getFrom().getLastName());

            if (hasCallback) {
                DispatcherResponse resp = dispatcherService.getResponseByCallback(chatId, data);
                sendMenu(chatId, resp.getMenu());

            } else if (!userService.isUserExistsInRepoByChatId(chatId)) {
                try {
                    if (userService.isUserExistsInResourceByChatId(chatId)) {
                            userService.addNew(chatId);
                    } else {
                        // TODO: Add registration via auth-service, when it will be completed
                        // async user creating request
                        // send message with registration links

                        // Temp solution
                        dispatcherService.createUserStorage(chatId, userName);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    executeSendingMessage(chatId, SOMETHING_WENT_WRONG.getText());
                }

                String welcomeMessage = (WELCOME.getText() + HOW_IT_WORKS.getText()).replaceAll("\\{name}", userName);

                executeSendingMessage(chatId, welcomeMessage);
                sendMenu(chatId, dispatcherService.getFirstMenu(chatId));

            } else if (ECommand.getCommandByMessage(text) != null) {
                // /start /help /howitworks ...
                ECommand command = ECommand.getCommandByMessage(text);

                executeSendingMessage(chatId, command.getMessageText().replaceAll("\\{name}", userName));
                sendMenu(chatId, dispatcherService.getResponseByCommand(chatId, command).getMenu());

            } else if (EPlaceholderCommand.getPlaceholderCommandByPref(text) != null) {
                // #add, #update, #delete ...
                DispatcherResponse resp = dispatcherService.getResponseByPlaceholderCommand(chatId, text);

                executeSendingMessage(chatId, resp.getStatus().getText());
                sendMenu(chatId, resp.getMenu());

            } else if (EKeyboardCommand.getKeyboardCommandByMessage(text) != null) {
                // Keyboard buttons
                EKeyboardCommand command = EKeyboardCommand.getKeyboardCommandByMessage(text);

                DispatcherResponse resp = dispatcherService.getResponseByKeyboardCommand(chatId, command);
                if (command == EKeyboardCommand.GET_INFO)
                    executeSendingMessage(chatId, resp.getMenu().getInfoText());

                if (resp.getMenu() instanceof SelfCheckMenu) {
                    executeSendingMenu(chatId, resp.getMenu(), command != EKeyboardCommand.NEXT, true);
                    if (resp.isNeedSendStatus()) executeSendingMessage(chatId, resp.getStatus().getText());

                } else if (command == EKeyboardCommand.NEXT && resp.getMenu() instanceof AStudyingMenu) {
                    executeSendingMenu(chatId, resp.getMenu(), false, false);
                    if (resp.isNeedSendStatus()) executeSendingMessage(chatId, resp.getStatus().getText());

                } else if (command == EKeyboardCommand.NEXT && !(resp.getMenu() instanceof AStudyingMenu)) {
                    executeSendingMessage(chatId, resp.getStatus().getText());
                    sendMenu(chatId, resp.getMenu());

                } else if (command == EKeyboardCommand.SKIP) {
                    sendCorrectAnswer(chatId, resp.getMenu(), resp.getTestResult());
                    if (resp.isNeedSendStatus()) executeSendingMessage(chatId, resp.getStatus().getText());
                    executeSendingMenu(chatId, resp.getMenu(), false, false);

                } else sendMenu(chatId, resp.getMenu());

            } else if (dispatcherService.isUserCurrentMenuStudying(chatId)) {
                // Perhaps, it will be a user test answer
                DispatcherResponse resp = dispatcherService.checkAnswer(chatId, text);

                if (!resp.getTestResult().getRightAnswer())
                    sendCorrectAnswer(chatId, resp.getMenu(), resp.getTestResult());

                // If we continue testing
                if (resp.getMenu() instanceof AStudyingMenu) {
                    executeSendingMenu(chatId, resp.getMenu(), false, false);
                } else {
                    executeSendingMessage(chatId, COMPLETE_SET.getText());
                    executeSendingMenu(chatId, resp.getMenu(), true, false);
                }

            } else {
                executeSendingMessage(chatId, BAD_REQUEST.getText());
                DispatcherResponse resp = dispatcherService.getLastMenu(chatId);
                sendMenu(chatId, resp.getMenu());
            }
            log.info("\n---\n");
        }
    }

    private void sendMenu(Long chatId, Menu menu) {
        executeSendingMenu(chatId, menu, true, false);
    }

    private void executeSendingMenu(Long chatId, Menu menu, Boolean needSendTitle, Boolean enableMDV2) {

        if (needSendTitle) {
            SendMessage titleMessage = SendMessage.builder()
                    .chatId(chatId)
                    .replyMarkup(menu.getKeyboard())
                    .text(menu.getTitle())
                    .build();
            titleMessage.enableMarkdown(true);

            try {
                execute(titleMessage);
            } catch (TelegramApiException e) {
                executeSendingMessageWithoutMD(chatId, titleMessage);
            }
        }

        SendMessage textMessage = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(needSendTitle ? menu.getInlineKeyboard() : menu.getKeyboard())
                .text(menu.getText())
                .build();

        if (enableMDV2) {
            // TODO: complete MDV2 handling
            String str = menu.getText()
                    .replaceAll("-", ":")
                    .replaceAll("\\(", "[")
                    .replaceAll("\\)", "]")
                    .replaceAll("\\.", ";");

            textMessage.setText(str);
            textMessage.enableMarkdownV2(true);
        } else textMessage.enableMarkdown(true);

        try {
            execute(textMessage);
        } catch (Exception e) {
            e.printStackTrace();
            executeSendingMessageWithoutMD(chatId, textMessage);
        }

    }

    private void executeSendingMessage(Long chatId, String text) {
        SendMessage messageWithKeyboard = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        messageWithKeyboard.enableMarkdown(true);

        try {
            execute(messageWithKeyboard);
        } catch (TelegramApiException e) {
            executeSendingMessageWithoutMD(chatId, messageWithKeyboard);
        }
    }

    private void executeSendingMessageWithoutMD(Long chatId, SendMessage message) {
        log.debug("--- Can't send message \n" +
                "chatId:" + chatId + "\n" +
                "message.getReplyMarkup(): " + message.getReplyMarkup() + "\n" +
                "message.getParseMode(): " + message.getParseMode() + "\n" +
                "message.getText(): " + message.getText() + "\n");

        message.enableMarkdown(false);
        message.enableMarkdownV2(false);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.debug("--- Can't send message without MD and MDV2:\n" + message);
            e.printStackTrace();

            executeSendingMessage(chatId, SOMETHING_WENT_WRONG.getText());
            DispatcherResponse resp = dispatcherService.getLastMenu(chatId);
            sendMenu(chatId, resp.getMenu());
        }
    }

    private void sendCorrectAnswer(Long chatId, Menu menu, TestResult result) {
        String correctAnswer;
        Card card = result.getCard();

        // We can't show last card in a correct queue because we have only next menu
        if (menu instanceof AStudyingMenu) {
            AStudyingMenu studyingMenu = (AStudyingMenu) menu;
            correctAnswer = studyingMenu.getMode().isFromKeyMode()
                    ? card.getCardKey() + " : " + card.getCardValue()
                    : card.getCardValue() + " : " + card.getCardKey();

        } else correctAnswer = card.getCardKey() + " : " + card.getCardValue();

        executeSendingMessage(chatId, "❗" + correctAnswer + "❗");
    }

}

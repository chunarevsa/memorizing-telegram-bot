package org.memorizing.controller;

import org.apache.log4j.Logger;
import org.memorizing.model.command.ECommand;
import org.memorizing.model.command.EKeyboardCommand;
import org.memorizing.model.command.EPlaceholderCommand;
import org.memorizing.model.menu.AStudyingMenu;
import org.memorizing.model.menu.MenuFactory;
import org.memorizing.model.menu.SelfCheckMenu;
import org.memorizing.resource.cardApi.CardDto;
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


    /**
     * hasCallback
     * 1) Title(+inline)
     * 2) next menu(+keyboard)
     * <p>
     * !users.containsKey(chatId)
     * 1) Regular
     * 2) Title(+inline)
     * 3) CardStocksMenu text(+keyboard)
     * <p>
     * /start /help /howitworks
     * 1) Regular
     * 2) Если Start -> Send: Title(+inline) + MAIN(CARD_STOCKS)
     * 2) Если other -> Send: Title(+inline) + CURRENT
     * <p>
     * #add-CardStock ...
     * 1) Status
     * 2) Title(+inline)
     * 3) Last menu text (+keyboard)
     * <p>
     * Keyboard buttons
     * GET_INFO
     * 1) Info text
     * 2) Title(+inline)
     * 3) Menu text(+keyboard)
     * <p>
     * NEXT
     * 1) Menu text(+keyboard) (MDV2)
     * 2) Если resp.isNeedSendStatus() Status
     * <p>
     * SKIP
     * 1) Correct answer
     * 2) Если resp.isNeedSendStatus() Status
     * 3) Menu text(+keyboard)
     * <p>
     * OTHER
     * 1) Title(+inline)
     * 2) Menu text(+keyboard)
     * <p>
     * isUserCurrentMenuStudying
     * 1) Correct answer
     * 2) Если NEXT == AStudyingMenu -> Menu text(+keyboard)
     * 2) Если NEXT != AStudyingMenu -> Regular
     * 3) Title(+inline)
     * 4) Menu text(+keyboard)
     * <p>
     * else
     * 1) Status
     * 2) Title(+inline)
     * 3) Menu text(+keyboard)
     */

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

                } else if (userService.isUserExistsByChatId(chatId)) {
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
                    if (command == EKeyboardCommand.GET_INFO)
                        executeSendingMessage(chatId, resp.getMenu().getInfoText());

                    if (resp.getMenu() instanceof SelfCheckMenu) {

                        sendMenuWithoutTitleAndWithMDV2(chatId, resp.getMenu());
                        if (resp.isNeedSendStatus()) executeSendingMessage(chatId, resp.getStatus().getText());

                    } else if (command == EKeyboardCommand.NEXT && resp.getMenu() instanceof AStudyingMenu) {

                        sendMenuWithoutTitle(chatId, resp.getMenu());
                        if (resp.isNeedSendStatus()) executeSendingMessage(chatId, resp.getStatus().getText());

                    } else if (command == EKeyboardCommand.SKIP) {
                        String correctAnswer;
                        CardDto card = resp.getTestResult().getCard();

                        // We can't show last card in a correct queue because we have only next menu
                        if (resp.getMenu() instanceof AStudyingMenu) {
                            AStudyingMenu studyingMenu = (AStudyingMenu) resp.getMenu();
                            correctAnswer = studyingMenu.getMode().isFromKeyMode()
                                    ? card.getCardKey() + " : " + card.getCardValue()
                                    : card.getCardValue() + " : " + card.getCardKey();

                        } else correctAnswer = card.getCardKey() + " : " + card.getCardValue();

                        executeSendingMessage(chatId, "❗" + correctAnswer + "❗");
                        if (resp.isNeedSendStatus()) executeSendingMessage(chatId, resp.getStatus().getText());
                        sendMenuWithoutTitle(chatId, resp.getMenu());

                    } else sendMenu(chatId, resp.getMenu());

                } else if (messageDispatcherService.isUserCurrentMenuStudying(chatId)) {
                    // may be, it is user test answer
                    DispatcherResponse resp = messageDispatcherService.checkAnswer(chatId, text);

                    if (!resp.getTestResult().getRightAnswer()) {
                        // Create correct answer
                        String correctAnswer;
                        CardDto card = resp.getTestResult().getCard();

                        // We can't show last card in a correct queue because we have only next menu
                        if (resp.getMenu() instanceof AStudyingMenu) {
                            AStudyingMenu studyingMenu = (AStudyingMenu) resp.getMenu();
                            correctAnswer = studyingMenu.getMode().isFromKeyMode()
                                    ? card.getCardKey() + " : " + card.getCardValue()
                                    : card.getCardValue() + " : " + card.getCardKey();

                        } else correctAnswer = card.getCardKey() + " : " + card.getCardValue();

                        executeSendingMessage(chatId, "❗" + correctAnswer + "❗");
                    }

                    // If we continue testing
                    if (resp.getMenu() instanceof AStudyingMenu) {
                        sendMenu(chatId, resp.getMenu());
                    } else {
                        executeSendingMessage(chatId, COMPLETE_SET.getText());
                        sendMenu(chatId, resp.getMenu());
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

    private void sendMenuWithoutTitleAndWithMDV2(Long chatId, MenuFactory menu) {
        executeSendingMenu2(chatId, menu, false, true);
    }

    private void sendMenuWithoutTitle(Long chatId, MenuFactory menu) {
        executeSendingMenu2(chatId, menu, false, false);
    }

    private void sendMenu(Long chatId, MenuFactory menu) {
        executeSendingMenu2(chatId, menu, true, false);
    }

    private void executeSendingMenu2(Long chatId, MenuFactory menu, Boolean needSendTitle, Boolean enableMDV2) {
        try {
            if (needSendTitle) {
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
            if (enableMDV2) messageWithInlineKeyboard.enableMarkdownV2(true);
            execute(messageWithInlineKeyboard);

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

package org.memorizing.service;

import org.apache.log4j.Logger;
import org.memorizing.exception.BadRequestException;
import org.memorizing.model.command.ECommand;
import org.memorizing.model.command.EKeyboardCommand;
import org.memorizing.model.command.EPlaceholderCommand;
import org.memorizing.model.menu.AStudyingMenu;
import org.memorizing.model.menu.SelfCheckMenu;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
import java.util.function.BiConsumer;

import static org.memorizing.model.ERegularMessages.HOW_IT_WORKS;
import static org.memorizing.model.ERegularMessages.WELCOME;
import static org.memorizing.model.EStatus.COMPLETE_SET;

@Service
public class UpdateService {
    private static final Logger log = Logger.getLogger(UpdateService.class);
    private final DispatcherService dispatcherService;
    private final UserService userService;
    private final SendingService sendingService;

    public UpdateService(DispatcherService dispatcherService, UserService userService, SendingService sendingService) {
        this.dispatcherService = dispatcherService;
        this.userService = userService;
        this.sendingService = sendingService;
    }

    public void executeUpdate(Update update, BiConsumer<SendMessage, Long> method) throws Exception {
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
                sendingService.sendMenu(chatId, resp.getMenu(), method);

            } else if (!userService.isUserExistsInRepoByChatId(chatId)) {

                if (userService.isUserExistsInResourceByChatId(chatId)) {
                    userService.addNew(chatId);
                } else {
                    // TODO: Add registration via auth-service, when it will be completed
                    // async user creating request
                    // send message with registration links

                    // Temp solution
                    dispatcherService.createUserStorage(chatId, userName);

                }

                String welcomeMessage = (WELCOME.getText() + HOW_IT_WORKS.getText()).replaceAll("\\{name}", userName);

                sendingService.executeSendingMessage(chatId, welcomeMessage, method);
                sendingService.sendMenu(chatId, dispatcherService.getFirstMenu(chatId), method);

            } else if (ECommand.getCommandByMessage(text) != null) {
                // /start /help /howitworks ...
                ECommand command = ECommand.getCommandByMessage(text);

                sendingService.executeSendingMessage(chatId, command.getMessageText().replaceAll("\\{name}", userName), method);
                sendingService.sendMenu(chatId, dispatcherService.getResponseByCommand(chatId, command).getMenu(), method);

            } else if (EPlaceholderCommand.getPlaceholderCommandByPref(text) != null) {
                // #add, #update, #delete ...

                DispatcherResponse resp = dispatcherService.getResponseByPlaceholderCommand(chatId, text);

                sendingService.executeSendingMessage(chatId, resp.getStatus().getText(), method);
                sendingService.sendMenu(chatId, resp.getMenu(), method);

            } else if (EKeyboardCommand.getKeyboardCommandByMessage(text) != null) {
                // Keyboard buttons
                EKeyboardCommand command = EKeyboardCommand.getKeyboardCommandByMessage(text);
                DispatcherResponse resp = dispatcherService.getResponseByKeyboardCommand(chatId, command);

                if (command == EKeyboardCommand.GET_INFO)
                    sendingService.executeSendingMessage(chatId, resp.getMenu().getInfoText(), method);

                if (resp.getMenu() instanceof SelfCheckMenu) {
                    sendingService.executeSendingMenu(chatId, resp.getMenu(), command != EKeyboardCommand.NEXT, true, method);
                    if (resp.isNeedSendStatus()) sendingService.executeSendingMessage(chatId, resp.getStatus().getText(), method);

                } else if (command == EKeyboardCommand.NEXT && resp.getMenu() instanceof AStudyingMenu) {
                    sendingService.executeSendingMenu(chatId, resp.getMenu(), false, false, method);
                    if (resp.isNeedSendStatus()) sendingService.executeSendingMessage(chatId, resp.getStatus().getText(), method);

                } else if (command == EKeyboardCommand.NEXT && !(resp.getMenu() instanceof AStudyingMenu)) {
                    sendingService.executeSendingMessage(chatId, resp.getStatus().getText(), method);
                    sendingService.sendMenu(chatId, resp.getMenu(), method);

                } else if (command == EKeyboardCommand.SKIP) {
                    sendingService.sendCorrectAnswer(chatId, resp.getMenu(), resp.getTestResult(), method);
                    if (resp.isNeedSendStatus()) sendingService.executeSendingMessage(chatId, resp.getStatus().getText(), method);
                    sendingService.executeSendingMenu(chatId, resp.getMenu(), false, false, method);

                } else sendingService.sendMenu(chatId, resp.getMenu(), method);

            } else if (dispatcherService.isUserCurrentMenuStudying(chatId)) {
                // Perhaps, it will be a user test answer
                DispatcherResponse resp = dispatcherService.checkAnswer(chatId, text);

                if (!resp.getTestResult().getRightAnswer()) sendingService.sendCorrectAnswer(chatId, resp.getMenu(), resp.getTestResult(), method);

                // If we continue testing
                if (resp.getMenu() instanceof AStudyingMenu) {
                    sendingService.executeSendingMenu(chatId, resp.getMenu(), false, false, method);
                } else {
                    sendingService.executeSendingMessage(chatId, COMPLETE_SET.getText(), method);
                    sendingService.executeSendingMenu(chatId, resp.getMenu(), true, false, method);
                }

            } else {
                throw new BadRequestException(chatId);
            }
            log.info("\n---\n");
        }
    }

    public void createAndExecuteLastMenu(Long chatId, BiConsumer<SendMessage, Long> method) {
        DispatcherResponse lastMenu = dispatcherService.getLastMenu(chatId);
        sendingService.sendMenu(chatId, lastMenu.getMenu(), method);
    }
}

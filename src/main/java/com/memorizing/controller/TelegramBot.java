package com.memorizing.controller;

import org.apache.log4j.Logger;
import com.memorizing.config.TelegramBotConfig;
import com.memorizing.exception.AChatException;
import com.memorizing.service.SendingService;
import com.memorizing.service.UpdateService;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.function.BiConsumer;

import static com.memorizing.model.EStatus.SOMETHING_WENT_WRONG;

@Controller
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(TelegramBot.class);
    private final TelegramBotConfig config;
    private final UpdateService updateService;
    private final SendingService sendingService;
    private BiConsumer<SendMessage, Long> method;


    public TelegramBot(TelegramBotConfig config, UpdateService updateService, SendingService sendingService) {
        super(config.getBotToken());
        this.config = config;
        this.updateService = updateService;
        this.sendingService = sendingService;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            updateService.executeUpdate(update, getMethod());
        } catch (Exception e) {
            handleException(update.getMessage().getChatId(), e);
        }
    }

    protected void handleException(Long chatId, Exception ex) {
        if (ex instanceof AChatException) {
            sendingService.executeSendingMessage(chatId, ((AChatException) ex).getText(), getMethod());
        } else {
            log.error(ex.getMessage());
            sendingService.executeSendingMessage(chatId, SOMETHING_WENT_WRONG.getText(), getMethod());
        }
        updateService.createAndExecuteLastMenu(chatId, getMethod());
    }

    // Singleton
    private BiConsumer<SendMessage, Long> getMethod() {
        if (method == null) {
            method = (message, chatId) -> {
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    // one more sending attempt without MD
                    try {
                        sendingService.executeSendingMessageWithoutMD(chatId, message, method);
                    } catch (Exception ex) {
                        handleException(chatId, ex);
                    }
                }
            };
        }
        return method;
    }
}

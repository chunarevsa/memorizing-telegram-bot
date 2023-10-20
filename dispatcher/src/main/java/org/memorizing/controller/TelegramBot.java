package org.memorizing.controller;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingBot {


    @Override
    public String getBotUsername() {
        return "memorizing_telegram_bot";
    }

    @Override
    public String getBotToken() {
        return "6784016579:AAGLRgVj4z-ktU8DWlSt4SORX6Pdy0etZ_o";
    }

    @Override
    public void onUpdateReceived(Update update) {
        var message = update.getMessage();
        System.out.println(message.getText());
    }
}

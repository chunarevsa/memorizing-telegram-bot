package org.memorizing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    private final String botToken;
    private final String botName;

    public TelegramBotConfig(
            @Value("${telegram-bot.token}") String botToken,
            @Value("${telegram-bot.name}") String botName) {
        this.botToken = botToken;
        this.botName = botName;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    public String getBotToken() {
        return botToken;
    }

    public String getBotName() {
        return botName;
    }
}

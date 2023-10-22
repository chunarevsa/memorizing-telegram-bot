package org.memorizing.botinstance;

import org.apache.log4j.Logger;
import org.memorizing.controller.UpdateController;
import org.memorizing.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final String botName;

//    private final UpdateController updateController;
    private final TelegramBotsApi session;
    private final UsersRepo usersRepo;

    public TelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.name}") String botName,
//            UpdateController updateController,
            TelegramBotsApi session,
            UsersRepo usersRepo) {
        super(botToken);
        this.botName = botName;
//        this.updateController = updateController;
        this.session = session;
        this.usersRepo = usersRepo;
    }

    private static final Logger log = Logger.getLogger(TelegramBot.class);
    public static ConcurrentHashMap<Long, LocalDate> users = new ConcurrentHashMap<>();

    @PostConstruct
    void init() throws TelegramApiException {
        usersRepo.findAll().forEach(user -> TelegramBot.users.put(user.getChatId(), LocalDate.now()));
        log.debug("Users:" + users.toString());
//        session.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("onUpdateReceived");
        var message = update.getMessage();
//        updateController.processUpdate(update);
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}

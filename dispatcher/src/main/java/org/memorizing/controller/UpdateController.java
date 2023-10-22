package org.memorizing.controller;

import org.apache.log4j.Logger;
import org.memorizing.botinstance.TelegramBot;
import org.memorizing.utils.CardWebClientBuilder;
import org.memorizing.utils.MessageUtils;
import org.memorizing.utils.cardApi.StorageDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

//@Component
public class UpdateController {
    private static final Logger log = Logger.getLogger(UpdateController.class);
//    private final TelegramBot bot;
//    private final MessageUtils messageUtils;
//    private final CardWebClientBuilder cardWebClientBuilder;
//
//    public UpdateController(
//            TelegramBot bot,
//            MessageUtils messageUtils,
//            CardWebClientBuilder cardWebClientBuilder) {
//        this.bot = bot;
//        this.messageUtils = messageUtils;
//        this.cardWebClientBuilder = cardWebClientBuilder;
//    }

//    public void processUpdate(Update update) {
//        if (update == null) {
//            log.error("Received update is null");
//            return;
//        }
//
//        if (update.getMessage() != null) {
//            distributeMessageByType(update);
//        } else {
//            log.error("Unsupported message type is received" + update);
//        }
//    }

//    private void distributeMessageByType(Update update) {
//        var message = update.getMessage();
//        if (message.getText() != null) {
//            processTextMessage(update);
//        } else {
//            setUnsupportedMessageTypeView(update);
//        }
//    }

//    private void processTextMessage(Update update) {
//        Long chatId = update.getMessage().getChatId();
//        Integer userId = getUserIdByChatId(chatId);
//        StorageDto storageByUserId = cardWebClientBuilder.getStorageByUserId(userId);
//
//        SendMessage sendMessage;
//        if (storageByUserId == null || storageByUserId.getId() == null) {
//            sendMessage = messageUtils.generateSendMessageWithText(update, "not found");
//        } else {
//            sendMessage = messageUtils.generateSendMessageWithText(update, "storage id:" + storageByUserId.getId().toString());
//        }
//        setView(sendMessage);
//
//    }

//    private Integer getUserIdByChatId(Long chatId) {
//        // TODO : add finding userId by chatId
//        // may be, it will be internal DB (Redis)
//        // or request to user-service
//        return 1;
//    }

//    private void setUnsupportedMessageTypeView(Update update) {
//        var sendMessage =
//                messageUtils.generateSendMessageWithText(update, "Unsupported message type view");
//        setView(sendMessage);
//    }

//    private void setView(SendMessage sendMessage) {
//        bot.sendAnswerMessage(sendMessage);
//    }


}

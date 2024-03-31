package org.memorizing.service;

import org.memorizing.model.menu.AStudyingMenu;
import org.memorizing.model.menu.Menu;
import org.memorizing.model.storage.Card;
import org.memorizing.model.storage.TestResult;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.function.BiConsumer;

@Service
public class SendingService {

    public void sendMenu(Long chatId, Menu menu, BiConsumer<SendMessage, Long> method) {
        executeSendingMenu(chatId, menu, true, false, method);
    }

    public void executeSendingMenu(Long chatId, Menu menu, Boolean needSendTitle, Boolean enableMDV2, BiConsumer<SendMessage, Long> method) {
        if (needSendTitle) {
            SendMessage titleMessage = SendMessage.builder()
                    .chatId(chatId)
                    .replyMarkup(menu.getKeyboard())
                    .text(menu.getTitle())
                    .build();
            titleMessage.enableMarkdown(true);

            method.accept(titleMessage, chatId);
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

        method.accept(textMessage, chatId);
    }

    public void executeSendingMessage(Long chatId, String text, BiConsumer<SendMessage, Long> method) {
        SendMessage messageWithKeyboard = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        messageWithKeyboard.enableMarkdown(true);
        method.accept(messageWithKeyboard, chatId);
    }

    public void executeSendingMessageWithoutMD(Long chatId, SendMessage message, BiConsumer<SendMessage, Long> method) {
        message.enableMarkdown(false);
        message.enableMarkdownV2(false);
        method.accept(message, chatId);
    }

    public void sendCorrectAnswer(Long chatId, Menu menu, TestResult result, BiConsumer<SendMessage, Long> method) {
        String correctAnswer;
        Card card = result.getCard();

        // We can't show last card in a correct queue because we have only next menu
        if (menu instanceof AStudyingMenu) {
            AStudyingMenu studyingMenu = (AStudyingMenu) menu;
            correctAnswer = studyingMenu.getMode().isFromKeyMode()
                    ? card.getCardKey() + " : " + card.getCardValue()
                    : card.getCardValue() + " : " + card.getCardKey();

        } else correctAnswer = card.getCardKey() + " : " + card.getCardValue();

        executeSendingMessage(chatId, "❗" + correctAnswer + "❗", method);
    }

}

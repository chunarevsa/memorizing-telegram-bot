package org.memorizing.exception;

import org.memorizing.model.EStatus;

abstract class AChatException extends RuntimeException {

    private final Long chatId;
    private final String text;

    protected AChatException(Long chatId, EStatus status) {
        this.chatId = chatId;
        this.text = status.getText();
    }

    public Long getChatId() {
        return chatId;
    }

    public String getText() {
        return text;
    }
}

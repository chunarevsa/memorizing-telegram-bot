package com.memorizing.exception;

import com.memorizing.model.EStatus;

public abstract class AChatException extends RuntimeException {

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

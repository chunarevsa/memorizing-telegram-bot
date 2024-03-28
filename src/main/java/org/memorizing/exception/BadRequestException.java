package org.memorizing.exception;

import org.memorizing.model.EStatus;

public class BadRequestException extends AChatException{
    public BadRequestException(Long chatId) {
        super(chatId, EStatus.BAD_REQUEST);
    }
}

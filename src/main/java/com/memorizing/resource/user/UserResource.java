package com.memorizing.resource.user;

import com.memorizing.resource.user.rest.dto.UserDto;

public interface UserResource {
    UserDto getUserByChatId(Long chatId);

    boolean isUserExistsByChatId(Long chatId);
}

package org.memorizing.resource.user;

import org.memorizing.resource.user.rest.dto.UserDto;

public interface UserResource {
    UserDto getUserByChatId(Long chatId);

    boolean isUserExistsByChatId(Long chatId);
}

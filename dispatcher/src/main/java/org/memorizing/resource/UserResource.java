package org.memorizing.resource;

import org.memorizing.resource.cardApi.UserDto;

import java.util.List;

public interface UserResource {
    UserDto getUserByChatId(Long chatId);

    List<UserDto> getChatIdListWithUserId();
}

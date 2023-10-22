package org.memorizing.repository;

import org.memorizing.botinstance.UserDto;

import java.util.List;

public interface UserResource {
    UserDto getUserByChatId(Long chatId);

    List<UserDto> getChatIdListWithUserId();
}

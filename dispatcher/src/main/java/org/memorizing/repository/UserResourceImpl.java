package org.memorizing.repository;

import org.memorizing.botinstance.UserDto;
import org.memorizing.utils.UserWebClientBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserResourceImpl implements UserResource {
    private final UserWebClientBuilder userWebClientBuilder;

    public UserResourceImpl(UserWebClientBuilder userWebClientBuilder) {
        this.userWebClientBuilder = userWebClientBuilder;
    }

    @Override
    public UserDto getUserByChatId(Long chatId) {
        return userWebClientBuilder.getUserByChatId(chatId);
    }

    @Override
    public List<UserDto> getChatIdListWithUserId() {
        return userWebClientBuilder.getChatIdListWithUserId();
    }
}

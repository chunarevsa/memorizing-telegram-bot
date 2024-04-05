package com.memorizing.resource.user.rest;

import com.memorizing.resource.user.UserResource;
import com.memorizing.resource.user.rest.dto.UserDto;
import org.springframework.stereotype.Component;

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
    public boolean isUserExistsByChatId(Long chatId) {
        return userWebClientBuilder.isUserExistByChatId(chatId);
    }
}

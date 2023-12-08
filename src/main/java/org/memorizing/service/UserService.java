package org.memorizing.service;

import org.memorizing.entity.User;
import org.memorizing.repository.UsersRepo;
import org.memorizing.resource.UserResource;
import org.memorizing.resource.cardApi.UserDto;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UsersRepo usersRepo;
    private final UserResource userResource;

    public UserService(UsersRepo usersRepo, UserResource userResource) {
        this.usersRepo = usersRepo;
        this.userResource = userResource;
    }

    public Boolean isUserExistsByChatId(Long chatId) {
        Boolean isPresent = usersRepo.existsByChatId(chatId);
        if (!isPresent) {
            UserDto user = userResource.getUserByChatId(chatId);
            return user == null;
        } return true;
    }

    public User getByChatId(Long chatId) {
        return usersRepo.findByChatId(chatId);
    }

    public void save(User user) {
        usersRepo.save(user);
    }
}

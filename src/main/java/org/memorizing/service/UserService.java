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

    public Boolean isUserExistsInRepoByChatId(Long chatId) {
        return usersRepo.existsByChatId(chatId);
    }

    public User addNew(Long chatId) throws Exception {
        UserDto userDto = userResource.getUserByChatId(chatId);
        if (userDto == null) throw new Exception("not found");
        return usersRepo.save(new User(userDto.getChatId(), userDto.getTelegramUserName(), userDto.getStorageId()));
    }

    public User getByChatId(Long chatId) {
        return usersRepo.findByChatId(chatId);
    }

    public void save(User user) {
        usersRepo.save(user);
    }

    public boolean isUserExistsInResourceByChatId(Long chatId) {
        return userResource.isUserExistsByChatId(chatId);
    }
}

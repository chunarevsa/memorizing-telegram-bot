package com.memorizing.repository;

import com.memorizing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<User, Integer> {
    Boolean existsByChatId(Long chatId);

    User findByChatId(Long chatId);

}

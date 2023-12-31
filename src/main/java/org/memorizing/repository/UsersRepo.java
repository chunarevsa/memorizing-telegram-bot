package org.memorizing.repository;

import org.memorizing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<User, Integer> {
    Boolean existsByChatId(Long chatId);

    User findByChatId(Long chatId);

}

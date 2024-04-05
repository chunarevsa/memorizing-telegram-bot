package com.memorizing.repository;

import com.memorizing.entity.UserState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStateRepository extends JpaRepository<UserState, Integer> {

}

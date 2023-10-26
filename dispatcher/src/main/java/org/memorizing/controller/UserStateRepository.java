package org.memorizing.controller;

import org.memorizing.entity.UserState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStateRepository extends JpaRepository<UserState, Integer> {

}

package com.tareasPracticas.user_microservice.repository;

import com.tareasPracticas.user_microservice.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    UserEntity save(UserEntity entity);

    Optional<UserEntity> findById(String id);

    Optional<UserEntity> findByUsername(String username);

    boolean logoutById(String Id);

    List<UserEntity> findAll();
}

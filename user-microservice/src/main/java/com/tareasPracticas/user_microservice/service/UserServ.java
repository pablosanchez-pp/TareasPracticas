package com.tareasPracticas.user_microservice.service;

import com.tareasPracticas.user_microservice.entity.UserEntity;
import com.tareasPracticas.user_microservice.model.UserCreateRequest;
import com.tareasPracticas.user_microservice.model.UserLoginRequest;
import com.tareasPracticas.user_microservice.model.UserResponse;
import java.util.List;

public interface UserServ {
    UserResponse register(UserCreateRequest req);

    UserResponse login(UserLoginRequest req);

    void logout(String id);

    List<UserResponse> mostrarTodos();
}

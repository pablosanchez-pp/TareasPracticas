package com.tareasPracticas.user_microservice.mapper;

import com.tareasPracticas.user_microservice.entity.UserEntity;
import com.tareasPracticas.user_microservice.model.UserCreateRequest;
import com.tareasPracticas.user_microservice.model.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "PK", ignore = true)
    @Mapping(target = "SK", ignore = true)
    @Mapping(target = "GIndex1Pk", ignore = true)
    UserEntity toEntity(UserCreateRequest req);

    @Mapping(source = "createdDate", target = "createdAt")
    UserResponse toResponse(UserEntity entity);
}
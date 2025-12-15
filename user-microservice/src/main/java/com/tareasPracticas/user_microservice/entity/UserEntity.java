package com.tareasPracticas.user_microservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class UserEntity extends MainTable {
    private String username;
    private String password;
    private boolean active = true;
}
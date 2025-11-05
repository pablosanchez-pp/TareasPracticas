package com.tareasPracticas.client_microservice.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ClientEntity extends MainTable{
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String cifNifNie;

    private String keyWordSearch;
}

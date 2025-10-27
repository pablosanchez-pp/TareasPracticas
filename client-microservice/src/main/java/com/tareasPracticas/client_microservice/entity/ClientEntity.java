package com.tareasPracticas.client_microservice.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class ClientEntity extends MainTable{
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private String cifNifNie;

    @DynamoDbAttribute("nombre")
    public String getName() { return nombre; }

    @DynamoDbAttribute("apellido")
    public String getSurname() { return apellido; }

    @DynamoDbAttribute("telefono")
    public String getPhone() { return telefono; }

    @DynamoDbAttribute("email")
    public String getEmail() { return email; }

    @DynamoDbAttribute("cifNifNie")
    public String getcifNifNie() { return cifNifNie; }

    @DynamoDbAttribute("direccion")
    public String getDir() { return direccion; }
}

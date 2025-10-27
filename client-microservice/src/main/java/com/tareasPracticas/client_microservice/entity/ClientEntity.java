package com.tareasPracticas.client_microservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity {
    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
}

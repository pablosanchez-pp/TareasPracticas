package com.tareasPracticas.client_microservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientOut {
    private String id;
    private String nombre;
    private String apellido;
    private String cifNifNie;
    private String telefono;
    private String email;
    private String status;
}


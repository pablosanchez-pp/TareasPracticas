package com.tareasPracticas.client_microservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientOut {
    private String id;
    private String name;
    private String surname;
    private String cifNifNie;
    private String phone;
    private String email;
    private String status;
}


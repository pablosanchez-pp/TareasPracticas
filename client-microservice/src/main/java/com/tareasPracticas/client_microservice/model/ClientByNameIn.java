package com.tareasPracticas.client_microservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientByNameIn {
    @NotBlank private String nombre;
}

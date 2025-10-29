package com.tareasPracticas.client_microservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientIn {
    @NotBlank private String nombre;
    @NotBlank private String apellido;
    @NotBlank private String cifNifNie;
    @NotBlank private String telefono;
    @Pattern(regexp="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message="Invalid email format")
    private String email;
}

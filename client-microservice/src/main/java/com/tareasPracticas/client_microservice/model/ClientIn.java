package com.tareasPracticas.client_microservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Modelo de entrada para crear/actualizar clientes")
@Data @NoArgsConstructor @AllArgsConstructor
public class ClientIn {
    @Schema(description = "Nombre del cliente")
    private String name;

    @Schema(description = "Apellido del cliente")
    private String surname;

    @Schema(description = "Documento identificativo (DNI/NIE/CIF)")
    private String cifNifNie;

    @Schema(description = "Teléfono de contacto")
    private String phone;

    @Schema(description = "Correo electrónico del cliente")
    private String email;
}

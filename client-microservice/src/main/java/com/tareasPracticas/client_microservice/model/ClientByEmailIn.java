package com.tareasPracticas.client_microservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Petición para buscar un cliente por email")
@Data @NoArgsConstructor @AllArgsConstructor
public class ClientByEmailIn {
    @Schema(description = "Email del cliente a buscar")
    private String email;
}

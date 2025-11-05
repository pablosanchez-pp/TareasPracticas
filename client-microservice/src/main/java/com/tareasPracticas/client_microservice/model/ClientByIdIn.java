package com.tareasPracticas.client_microservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Petición para obtener cliente por ID")
@Data @NoArgsConstructor @AllArgsConstructor
public class ClientByIdIn {
    @Schema(description = "Identificador del cliente")
    private String id;

    @Schema(description = "Si es true devuelve una salida simple")
    private Boolean simpleOutput;
}

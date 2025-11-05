package com.tareasPracticas.client_microservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Petición de búsqueda por nombre (like)")
@Data @NoArgsConstructor @AllArgsConstructor
public class ClientByNameIn {
    @Schema(description = "Texto a buscar dentro del nombre")
    private String query;
}

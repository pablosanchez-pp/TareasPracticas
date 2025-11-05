package com.tareasPracticas.client_microservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Resultado booleano de existencia")
@Data @NoArgsConstructor @AllArgsConstructor
public class ExistsOut {
    @Schema(description = "Indica si la entidad existe")
    private boolean exists;
}

package com.tareasPracticas.merchant_microservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo de salida simplificado que solo contiene el ID del merchant")
public class MerchantSimpleOut {

    @Schema(description = "Identificador único del merchant")
    private String id;
}

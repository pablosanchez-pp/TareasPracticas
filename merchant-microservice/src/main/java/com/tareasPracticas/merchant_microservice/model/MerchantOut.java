package com.tareasPracticas.merchant_microservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo de salida con la información completa del merchant")
public class MerchantOut {

    @Schema(description = "Identificador único del merchant")
    private String id;

    @Schema(description = "Nombre del merchant")
    private String name;

    @Schema(description = "Dirección física o comercial del merchant")
    private String address;

    @Schema(description = "Tipo de merchant (enum definido en MerchantIn)")
    private MerchantIn.MerchantType merchantType;

    @Schema(description = "Estado actual del merchant (ej. ACTIVE)")
    private String status;

    @Schema(description = "Fecha de creación del merchant")
    private Instant createdDate;
}

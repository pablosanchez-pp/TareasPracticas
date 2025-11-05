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
@Schema(description = "Modelo de entrada para crear o actualizar un merchant")
public class MerchantIn {

    @Schema(description = "Nombre del merchant")
    private String name;

    @Schema(description = "Dirección física o comercial del merchant")
    private String address;

    @Schema(description = "Tipo de merchant (PERSONAL_SERVICES o FINANCIAL_SERVICES)")
    private MerchantType merchantType;

    public enum MerchantType {
        MERCHANT_TYPE_PERSONAL_SERVICES,
        MERCHANT_TYPE_FINANCIAL_SERVICES
    }
}

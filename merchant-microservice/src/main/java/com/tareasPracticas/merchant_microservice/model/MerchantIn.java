package com.tareasPracticas.merchant_microservice.model;

import com.tareasPracticas.merchant_microservice.entity.MerchantType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantIn {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La direccion es obligatoria")
    private String dir;

    @NotNull(message = "El tipo de merchant es obligaorio")
    private MerchantType merchantType;
}

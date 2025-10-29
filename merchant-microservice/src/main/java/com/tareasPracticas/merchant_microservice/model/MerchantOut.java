package com.tareasPracticas.merchant_microservice.model;

import com.tareasPracticas.merchant_microservice.entity.MerchantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantOut {
    private String id;
    private String nombre;
    private String dir;
    private MerchantType merchantType;
    private String status;
    private Instant createDate;

}

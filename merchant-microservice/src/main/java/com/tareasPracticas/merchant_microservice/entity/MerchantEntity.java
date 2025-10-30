package com.tareasPracticas.merchant_microservice.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class MerchantEntity extends MainTable {

    private String nombre;
    private String dir;
    private MerchantType merchantType;

    @DynamoDbAttribute("nombre")
    public String getNombre() {
        return nombre;
    }

    @DynamoDbAttribute("dir")
    public String getDir() {
        return dir;
    }

    @DynamoDbAttribute("merchantType")
    public MerchantType getMerchantType(){
        return merchantType;
    }
}
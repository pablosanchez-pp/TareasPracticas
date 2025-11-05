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
    private String address;
    private MerchantType merchantType;

    private String keyWordSearch;

    @DynamoDbAttribute("name")
    public String getName() {
        return nombre;
    }

    @DynamoDbAttribute("address")
    public String getAdress() {
        return address;
    }

    @DynamoDbAttribute("merchantType")
    public MerchantType getMerchantType(){
        return merchantType;
    }
}
package com.tareasPracticas.merchant_microservice.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class MerchantEntity extends MainTable {

    private String name;
    private String address;
    private MerchantType merchantType;

    private String keyWordSearch;

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    @DynamoDbAttribute("address")
    public String getAdress() {
        return address;
    }

    @DynamoDbAttribute("merchantType")
    public MerchantType getMerchantType(){
        return merchantType;
    }

    @DynamoDbSecondarySortKey(indexNames = "GSI1")
    public String getKeyWordSearch() {
        return keyWordSearch;
    }
}
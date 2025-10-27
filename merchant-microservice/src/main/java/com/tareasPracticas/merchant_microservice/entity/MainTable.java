package com.tareasPracticas.merchant_microservice.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@DynamoDbBean
public class MainTable {

    private String PK;           // p.ej. CLIENT#<id>
    private String SK;           // p.ej. METADATA
    private String id;           // UUID lógico
    private String status;       // ACTIVE/INACTIVE
    private String gIndex2Pk;    // para GSI2
    private Instant createdDate;

    @DynamoDbPartitionKey
    public String getPK() { return PK; }

    @DynamoDbSortKey
    public String getSK() { return SK; }

    @DynamoDbSecondaryPartitionKey(indexNames = "GSI2")
    public String getGIndex2Pk() { return gIndex2Pk; }
}

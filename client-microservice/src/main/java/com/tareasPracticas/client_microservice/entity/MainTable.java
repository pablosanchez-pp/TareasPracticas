package com.tareasPracticas.client_microservice.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@DynamoDbBean
public class MainTable {

    private String PK;
    private String SK;
    private String id;
    private String status;
    private String gIndex2Pk;
    private String gIndex1Pk;
    private Instant createdDate;

    @DynamoDbPartitionKey
    public String getPK() { return PK; }

    @DynamoDbSortKey
    public String getSK() { return SK; }

    @DynamoDbAttribute("gIndex2Pk")
    @DynamoDbSecondaryPartitionKey(indexNames = "GSI2")
    public String getGIndex2Pk() { return gIndex2Pk; }

    @DynamoDbAttribute("gIndex1Pk")
    @DynamoDbSecondaryPartitionKey(indexNames = "GSI1")
    public String getGIndex1Pk() { return gIndex1Pk; }

}

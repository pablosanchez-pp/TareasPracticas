package com.tareasPracticas.merchant_microservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("init-table") // <-- solo se ejecuta si activas este perfil
public class DynamoDbTableInitializer {

    private final DynamoDbClient   dynamoDbClient;

    @Value("${app.dynamodb.table-name}")
    private String tableName;

    @PostConstruct
    public void init() {
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
            log.info("Table '{}' already exists", tableName);
            return;
        } catch (ResourceNotFoundException e) {
            log.info("Creating table '{}'", tableName);
        }

        CreateTableRequest req = CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(
                        KeySchemaElement.builder().attributeName("PK").keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName("SK").keyType(KeyType.RANGE).build()
                )
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("PK").attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName("SK").attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName("gIndex2Pk").attributeType(ScalarAttributeType.S).build()
                )
                .globalSecondaryIndexes(
                        GlobalSecondaryIndex.builder()
                                .indexName("GSI2")
                                .keySchema(KeySchemaElement.builder()
                                        .attributeName("gIndex2Pk").keyType(KeyType.HASH).build())
                                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                .provisionedThroughput(ProvisionedThroughput.builder()
                                        .readCapacityUnits(5L).writeCapacityUnits(5L).build())
                                .build()
                )
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(5L).writeCapacityUnits(5L).build())
                .build();

        dynamoDbClient.createTable(req);
        dynamoDbClient.waiter().waitUntilTableExists(
                DescribeTableRequest.builder().tableName(tableName).build());
        log.info("Table '{}' created", tableName);
    }
}

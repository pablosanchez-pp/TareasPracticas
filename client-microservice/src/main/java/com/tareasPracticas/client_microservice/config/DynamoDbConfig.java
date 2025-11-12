package com.tareasPracticas.client_microservice.config;

import com.tareasPracticas.client_microservice.entity.ClientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class DynamoDbConfig {

    @Value("${aws.region:eu-west-1}")
    private String region;

    @Value("${aws.dynamodb.endpoint:http://localhost:8000}")
    private String endpoint;

    @Value("${app.dynamodb.table:MainTable}")
    private String tableName;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("dummy", "dummy")
                        )
                )
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient enhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
    }

    @Bean
    DynamoDbTable<ClientEntity> clientTable(
            DynamoDbEnhancedClient enhanced,
            @Value("${app.dynamodb.table:MainTable}") String tableName
    ) {
        return enhanced.table(tableName, TableSchema.fromBean(ClientEntity.class));
    }
}
package com.tareasPracticas.merchant_microservice.repository;

import com.tareasPracticas.merchant_microservice.entity.MerchantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MerchantRepositoryImpl implements MerchantRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    private DynamoDbTable<MerchantEntity> table() {
        return enhancedClient.table("MainTable", TableSchema.fromBean(MerchantEntity.class));
    }

    @Override
    public MerchantEntity save(MerchantEntity entity) {
        table().putItem(entity);
        return entity;
    }

    @Override
    public List<MerchantEntity> findByNameContainingIgnoreCase(String nombre) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":frag", AttributeValue.builder().s(nombre).build()); // <-- SDK v2

        Expression exp = Expression.builder()
                .expression("contains(#n, :frag)")
                .expressionNames(Map.of("#n", "nombre")) // si tu campo es "nombre"; usa "name" si tu campo es name
                .expressionValues(values)
                .build();

        ScanEnhancedRequest scan = ScanEnhancedRequest.builder()
                .filterExpression(exp)
                .build();

        PageIterable<MerchantEntity> pages = table().scan(scan);
        List<MerchantEntity> parcial = pages.items().stream().toList();

        // Filtro final para ignorar mayúsculas/minúsculas
        final String needle = nombre.toLowerCase(Locale.ROOT);
        return parcial.stream()
                .filter(m -> m.getNombre() != null && m.getNombre().toLowerCase(Locale.ROOT).contains(needle))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MerchantEntity> findById(String id) {
        return Optional.ofNullable(table().getItem(r -> r.key(k -> k.partitionValue(id))));
    }
}

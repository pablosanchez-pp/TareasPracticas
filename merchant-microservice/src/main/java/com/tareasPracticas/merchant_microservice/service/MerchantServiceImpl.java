package com.tareasPracticas.merchant_microservice.service;

import com.tareasPracticas.merchant_microservice.entity.MerchantEntity;
import com.tareasPracticas.merchant_microservice.mappers.MerchantMapper;
import com.tareasPracticas.merchant_microservice.model.MerchantIn;
import com.tareasPracticas.merchant_microservice.model.MerchantOut;
import com.tareasPracticas.merchant_microservice.model.MerchantSimpleOut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final DynamoDbEnhancedClient enhancedClient;
    private final MerchantMapper mapper;

    @Value("${app.dynamodb.table:MainTable}")
    private String tableName;

    private DynamoDbTable<MerchantEntity> table() {
        return enhancedClient.table(tableName, TableSchema.fromBean(MerchantEntity.class));
    }

    @Override
    public MerchantOut create(MerchantIn in) {
        MerchantEntity entity = mapper.toEntity(in);
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        table().putItem(entity);
        return mapper.toOut(entity);
    }

    @Override
    public MerchantOut findById(String id) {
        MerchantEntity found = table().getItem(Key.builder().partitionValue(id).build());
        if (found == null) {
            throw new NoSuchElementException("Merchant not found: " + id);
        }
        return mapper.toOut(found);    }

    @Override
    public MerchantSimpleOut findSimpleById(String id) {
        MerchantEntity found = table().getItem(Key.builder().partitionValue(id).build());
        if (found == null) {
            throw new NoSuchElementException("Merchant not found: " + id);
        }
        return mapper.toSimpleOut(found);
    }

    @Override
    public List<MerchantOut> findByName(String name) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":frag", AttributeValue.builder().s(name).build());

        Expression exp = Expression.builder()
                .expression("contains(#n, :frag)")
                .expressionNames(Map.of("#n", "nombre"))
                .expressionValues(values)
                .build();

        ScanEnhancedRequest scan = ScanEnhancedRequest.builder()
                .filterExpression(exp)
                .build();

        PageIterable<MerchantEntity> pages = table().scan(scan);
        List<MerchantEntity> partial = pages.items().stream().toList();

        final String needle = name.toLowerCase(Locale.ROOT);
        return partial.stream()
                .filter(m -> m.getNombre() != null && m.getNombre().toLowerCase(Locale.ROOT).contains(needle))
                .map(mapper::toOut)
                .collect(Collectors.toList());
    }

    @Override
    public MerchantOut update(String id, MerchantIn in) {
        MerchantEntity existing = table().getItem(Key.builder().partitionValue(id).build());
        if (existing == null) {
            throw new NoSuchElementException("Merchant not found: " + id);
        }

        existing.setNombre(in.getNombre());
        existing.setDir(in.getDir());
        existing.setMerchantType(in.getMerchantType());

        table().putItem(existing);
        return mapper.toOut(existing);
    }
}

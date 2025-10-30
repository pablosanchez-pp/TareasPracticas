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
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
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

    private Key keyFor(String id) {
        return Key.builder()
                .partitionValue("MERCHANT#" + id)
                .sortValue("MERCHANT#" + id)
                .build();
    }

    @Override
    public MerchantOut create(MerchantIn in) {
        MerchantEntity entity = mapper.toEntity(in);

        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }

        entity.setPK("MERCHANT#" + entity.getId());
        entity.setSK("MERCHANT#" + entity.getId());

        if (entity.getNombre() != null) {
            entity.setGIndex2Pk("MERCHANT#NAME#" + entity.getNombre().toLowerCase(Locale.ROOT));
        }

        if (entity.getStatus() == null) entity.setStatus("ACTIVE");
        if (entity.getCreatedDate() == null) entity.setCreatedDate(Instant.parse(Instant.now().toString()));

        table().putItem(entity);

        return mapper.toOut(entity);
    }

    @Override
    public MerchantOut findById(String id) {
        MerchantEntity found = table().getItem(r -> r.key(keyFor(id)));
        if (found == null) {
            throw new NoSuchElementException("Merchant not found: " + id);
        }
        return mapper.toOut(found);    }

    @Override
    public MerchantSimpleOut findSimpleById(String id) {
        MerchantEntity found = table().getItem(r -> r.key(keyFor(id)));
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
        MerchantEntity existing = table().getItem(r -> r.key(keyFor(id)));
        if (existing == null) throw new NoSuchElementException("Merchant not found: " + id);

        mapper.updateEntityFromIn(in, existing);

        if (existing.getNombre() != null) {
            existing.setGIndex2Pk("MERCHANT#NAME#" + existing.getNombre().toLowerCase(Locale.ROOT));
        }

        table().putItem(existing);
        return mapper.toOut(existing);
    }

    public Optional<String> findClientIdOfMerchant(String merchantId) {
        String pk = "MERCHANT#" + merchantId;
        String prefix = "CLIENT#";

        var req = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.sortBeginsWith(
                        Key.builder().partitionValue(pk).sortValue(prefix).build()
                ))
                .limit(1)
                .build();

        var pageOpt = table().query(req).stream().findFirst();
        if (pageOpt.isEmpty()) return Optional.empty();
        var itemOpt = pageOpt.get().items().stream().findFirst();
        if (itemOpt.isEmpty()) return Optional.empty();

        String sk = itemOpt.get().getSK();
        return Optional.of(sk.substring(prefix.length()));
    }
}

package com.tareasPracticas.merchant_microservice.repository;

import com.tareasPracticas.merchant_microservice.entity.MerchantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MerchantRepositoryImpl implements MerchantRepository {

    private final DynamoDBMapper mapper;

    @Override
    public MerchantEntity save(MerchantEntity entity) {
        mapper.save(entity);
        return entity;
    }

    @Override
    public List<MerchantEntity> findByNameContainingIgnoreCase(String nombre) {
        Map<String,String> names = new HashMap<>();
        names.put("#n", "nombre");

        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":frag",new AttributeValue().witS(fragment));

        DynamoDBScanExpression scan = new DynamoDBScanExpression()
                .withFilterExpression("contains(#n, :frag)")
                .withExpressionAttributeNames(names)
                .withExpressionAttributeValues(values);

        List<MerchantEntity> partial = mapper.scan(MerchantEntity.class, scan);

        final String f = fragment.toLowerCase(Locale.ROOT);
        return partial.stream()
                .filter(m -> m.getNombre() != null && m.getNombre().toLowerCase(Locale.ROOT).contains(f))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MerchantEntity> findById(String id) {
        MerchantEntity key = new MerchantEntity();
        key.setId(id);
        MerchantEntity loaded = mapper.load(key);
        return Optional.ofNullable(loaded);
    }
}

package com.tareasPracticas.merchant_microservice.repository;

import com.tareasPracticas.merchant_microservice.entity.MerchantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.*;


@Repository
@RequiredArgsConstructor
public class MerchantRepositoryImpl implements MerchantRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    @Value("${app.dynamodb.table:MainTable}")
    private String tableName;

    private DynamoDbTable<MerchantEntity> table() {
        return enhancedClient.table("MainTable", TableSchema.fromBean(MerchantEntity.class));
    }

    private static String pk(String id) { return "MERCHANT#" + id; }
    private static String sk(String id) { return "MERCHANT#" + id; }

    @Override
    public MerchantEntity save(MerchantEntity entity) {
        table().putItem(entity);
        return entity;
    }
    @Override
    public Optional<MerchantEntity> findById(String id) {
        Key key = Key.builder().partitionValue(pk(id)).sortValue(sk(id)).build();
        return Optional.ofNullable(table().getItem(r -> r.key(key)));
    }

    private static String norm(String s) {
        if (s == null) return null;
        String n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return n.toLowerCase(java.util.Locale.ROOT).trim();
    }

    @Override
    public List<MerchantEntity> findByName(String q) {
        String normalized = norm(q);
        DynamoDbIndex<MerchantEntity> gsi1 = table().index("GSI1");

        QueryConditional qc = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue("MERCHANT#")
                        .sortValue(normalized)
                        .build()
        );

        List<MerchantEntity> out = new ArrayList<>();
        for (var page : gsi1.query(r -> r.queryConditional(qc))) {
            out.addAll(page.items());
        }
        return out;
    }

}

package com.tareasPracticas.merchant_microservice.service;

import com.tareasPracticas.merchant_microservice.entity.MerchantEntity;
import com.tareasPracticas.merchant_microservice.mappers.MerchantMapper;
import com.tareasPracticas.merchant_microservice.model.MerchantIn;
import com.tareasPracticas.merchant_microservice.model.MerchantOut;
import com.tareasPracticas.merchant_microservice.model.MerchantSimpleOut;
import com.tareasPracticas.merchant_microservice.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository repository;
    private final DynamoDbEnhancedClient enhancedClient;
    private final MerchantMapper mapper;

    @Value("${app.dynamodb.table:MainTable}")
    private String tableName;

    private static String norm(String s) {
        if (s == null) return null;
        String n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return n.toLowerCase(java.util.Locale.ROOT).trim();
    }

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
        MerchantEntity e = mapper.toEntity(in);

        if (e.getId() == null) e.setId(UUID.randomUUID().toString());

        e.setPK("MERCHANT#" + e.getId());
        e.setSK("MERCHANT#" + e.getId());
        e.setStatus("ACTIVE");
        e.setCreatedDate(Instant.now());

        // GSI de nombre
        e.setGIndex2Pk("MERCHANT#");
        e.setKeyWordSearch(norm(in.getName()));

        MerchantEntity saved = repository.save(e);
        return mapper.toOut(saved);
    }

    @Override
    public MerchantOut findById(String id) {
        MerchantEntity found = table().getItem(r -> r.key(keyFor(id)));
        if (found == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found");
        }
        return mapper.toOut(found);    }

    @Override
    public MerchantSimpleOut findSimpleById(String id) {
        MerchantEntity found = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Merchant not found: " + id));
        return mapper.toSimpleOut(found);
    }

    @Override
    public List<MerchantOut> findByName(String name) {
        List<MerchantEntity> hits = repository.findByName(name);
        return hits.stream().map(mapper::toOut).collect(Collectors.toList());
    }

    @Override
    public MerchantOut update(String id, MerchantIn in) {
        MerchantEntity existing = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Merchant not found: " + id));

        mapper.updateEntityFromIn(in, existing);

        existing.setPK("MERCHANT#" + id);
        existing.setSK("MERCHANT#" + id);

        existing.setGIndex2Pk("MERCHANT#");
        if (in.getName() != null) {
            existing.setKeyWordSearch(norm(in.getName()));
        }

        MerchantEntity saved = repository.save(existing);
        return mapper.toOut(saved);
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

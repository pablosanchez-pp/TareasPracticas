package com.tareasPracticas.client_microservice.service;

import com.tareasPracticas.client_microservice.entity.ClientEntity;
import com.tareasPracticas.client_microservice.entity.MainTable;
import com.tareasPracticas.client_microservice.exceptions.IdOnlyOut;
import com.tareasPracticas.client_microservice.exceptions.NotFoundException;
import com.tareasPracticas.client_microservice.feign.MerchantClient;
import com.tareasPracticas.client_microservice.mappers.ClientMapper;
import com.tareasPracticas.client_microservice.model.*;
import com.tareasPracticas.client_microservice.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;
    private final DynamoDbEnhancedClient enhancedClient;
    private final MerchantClient merchantClient;

    private final IdGenerator idGenerator = () -> UUID.randomUUID().toString();

    @Value("${app.dynamodb.table:MainTable}")
    private String tableName;

    private DynamoDbTable<MainTable> mainTable() {
        return enhancedClient.table(tableName, TableSchema.fromBean(MainTable.class));
    }

    private static String norm(String s) {
        if (s == null) return null;
        String n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return n.toLowerCase(java.util.Locale.ROOT).trim();
    }

    @Override
    public ClientOut create(ClientIn in) {
        ClientEntity entity = mapper.toEntity(in);

        if (entity.getId() == null)
            entity.setId(idGenerator.nextId());

        entity.setPK("CLIENT#" + entity.getId());
        entity.setSK("CLIENT#" + entity.getId());
        entity.setStatus("ACTIVE");
        entity.setCreatedDate(Instant.now());

        entity.setGIndex2Pk("CLIENT#");
        entity.setKeyWordSearch(norm(in.getNombre()));

        if (in.getEmail() != null) {
            String emailLower = in.getEmail().toLowerCase(Locale.ROOT);
            MainTable mt = (MainTable) entity;
            try {
                entity.getClass().getMethod("setGIndex1Pk", String.class)
                        .invoke(entity, "EMAIL#" + emailLower);
            } catch (Exception ignore) {
                entity.setGIndex2Pk("CLIENT#");
            }
        }

        ClientEntity saved = repository.save(entity);
        return mapper.toOut(saved);
    }

    @Override
    public Object findById(ClientByIdIn in) {
        String id = in.getId();
        ClientEntity entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found: " + id));

        if (Boolean.TRUE.equals(in.getSimpleOutPut())) {
            return new IdOnlyOut(entity.getId());
        }
        return mapper.toOut(entity);
    }

    @Override
    public List<ClientOut> findByName(ClientByNameIn in) {
        String q = in.getNombre();
        List<ClientEntity> hits = repository.findByName(q);
        return hits.stream().map(mapper::toOut).collect(Collectors.toList());
    }

    @Override
    public ClientOut findByEmail(ClientByEmailIn in) {
        String emailLower = in.getEmail().toLowerCase(Locale.ROOT);
        ClientEntity entity = repository.findByEmail(emailLower)
                .orElseThrow(() -> new NotFoundException("Client not found by email"));
        return mapper.toOut(entity);
    }

    @Override
    public ClientOut update(String id, ClientIn in) {
        ClientEntity existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found: " + id));

        mapper.updateEntityFromIn(in, existing);

        existing.setPK("CLIENT#" + id);
        existing.setSK("CLIENT#" + id);

        existing.setGIndex2Pk("CLIENT#");
        if (in.getNombre() != null) {
            existing.setKeyWordSearch(norm(in.getNombre()));
        }

        if (in.getEmail() != null) {
            String emailLower = in.getEmail().toLowerCase(Locale.ROOT);
            try {
                existing.getClass().getMethod("setGIndex1Pk", String.class)
                        .invoke(existing, "EMAIL#" + emailLower);
            } catch (Exception ignore) {

            }
        }

        ClientEntity saved = repository.save(existing);
        return mapper.toOut(saved);
    }

    interface IdGenerator {
        String nextId();
    }

    @Transactional
    public void linkClientToMerchant(String clientId, String merchantId) {
        merchantClient.findById(merchantId);

        MainTable edge1 = new MainTable();
        edge1.setPK("CLIENT#" + clientId);
        edge1.setSK("MERCHANT#" + merchantId);
        mainTable().putItem(edge1);

        MainTable edge2 = new MainTable();
        edge2.setPK("MERCHANT#" + merchantId);
        edge2.setSK("CLIENT#" + clientId);
        mainTable().putItem(edge2);
    }

    public List<String> listMerchantIdsOfClient(String clientId) {
        String pk = "CLIENT#" + clientId;
        String prefix = "MERCHANT#";

        var req = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.sortBeginsWith(
                        Key.builder().partitionValue(pk).sortValue(prefix).build()
                ))
                .build();

        List<String> ids = new ArrayList<>();
        mainTable().query(req).items().forEach(item -> {
            String sk = item.getSK();
            ids.add(sk.substring(prefix.length()));
        });
        return ids;
    }


}

package com.tareasPracticas.client_microservice.repository;

import com.tareasPracticas.client_microservice.entity.ClientEntity;
import com.tareasPracticas.client_microservice.model.ClientOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class ClientRepositoryDynamoDb implements ClientRepository{

    private final DynamoDbTable<ClientEntity> clientTable;

    private static String pk(String id) { return "CLIENT#" + id; }
    private static String sk(String id) { return "CLIENT#" + id;}

        @Override
    public ClientEntity save(ClientEntity entity) {
        clientTable.putItem(entity);
        return entity;
    }

    @Override
    public Optional<ClientEntity> findById(String id) {
        Key key = Key.builder().partitionValue(pk(id)).sortValue(sk(id)).build();
        return Optional.ofNullable(clientTable.getItem(r -> r.key(key)));
    }

    private static String normEmail(String s) {
        if (s == null) return null;
        return s.toLowerCase(Locale.ROOT).trim();
    }

    @Override
    public Optional<ClientEntity> findByEmail(String email) {
        String normEmail = normEmail(email);
        if (normEmail == null || normEmail.isEmpty()) return Optional.empty();

        DynamoDbIndex<ClientEntity> gsi = clientTable.index("GSI2");
        String g = "EMAIL#" + normEmail;

        return gsi.query(r -> r.queryConditional(
                        QueryConditional.keyEqualTo(Key.builder().partitionValue(g).build())))
                .stream()
                .flatMap(p -> p.items().stream())
                .findFirst();
    }

    private static String norm(String s) {
        if (s == null) return null;
        String n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return n.toLowerCase(java.util.Locale.ROOT).trim();
    }

    @Override
    public List<ClientEntity> findByName(String q) {
        String normalized = norm(q);
        if (normalized == null || normalized.isBlank()) return List.of();

        DynamoDbIndex<ClientEntity> gsi1 = clientTable.index("GSI1");

        QueryConditional qc = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue("CLIENT#")   // PK del GSI1
                        .sortValue(normalized)       // SK del GSI1 (prefijo)
                        .build()
        );

        List<ClientEntity> out = new ArrayList<>();
        gsi1.query(r -> r.queryConditional(qc))
                .stream().forEach(p -> out.addAll(p.items()));
        return out;
    }

    @Override
    public List<ClientEntity> findAll() {
        DynamoDbIndex<ClientEntity> gsi1 = clientTable.index("GSI1");

        // PK del índice = "CLIENT#"; sin condición de sort → devuelve todos
        QueryConditional qc = QueryConditional.keyEqualTo(
                Key.builder().partitionValue("CLIENT#").build()
        );

        List<ClientEntity> out = new ArrayList<>();
        gsi1.query(r -> r.queryConditional(qc))
                .stream()
                .forEach(page -> out.addAll(page.items()));
        return out;
    }
}

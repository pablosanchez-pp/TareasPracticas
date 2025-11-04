package com.tareasPracticas.client_microservice.repository;

import com.tareasPracticas.client_microservice.entity.ClientEntity;
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

    @Override
    public Optional<ClientEntity> findByEmail(String email) {
        DynamoDbIndex<ClientEntity> gsi = clientTable.index("GSI2");
        String g = "EMAIL#" + (email == null ? "" : email.toLowerCase(Locale.ROOT));
        return gsi.query(r -> r.queryConditional(
                        QueryConditional.keyEqualTo(Key.builder().partitionValue(g).build())))
                .stream().flatMap(p -> p.items().stream()).findFirst();
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
        DynamoDbIndex<ClientEntity> gsi1 = clientTable.index("GSI1");

        QueryConditional qc = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue("CLIENT#")
                        .sortValue(normalized)
                        .build()
        );

        List<ClientEntity> out = new ArrayList<>();
        for (var page : gsi1.query(r -> r.queryConditional(qc))) {
            page.items().forEach(out::add);
        }
        return out;
    }
}

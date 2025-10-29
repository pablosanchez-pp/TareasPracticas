package com.tareasPracticas.client_microservice.repository;

import com.tareasPracticas.client_microservice.entity.ClientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

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
        DynamoDbIndex<ClientEntity> gsi = clientTable.index("GSI2"); // GSI por gIndex2Pk
        String g = "EMAIL#" + (email == null ? "" : email.toLowerCase(Locale.ROOT));
        return gsi.query(r -> r.queryConditional(
                        QueryConditional.keyEqualTo(Key.builder().partitionValue(g).build())))
                .stream().flatMap(p -> p.items().stream()).findFirst();
    }

    @Override
    public List<ClientEntity> findByName(String nameLike) {
        String q = nameLike == null ? "" : nameLike.toLowerCase(Locale.ROOT);
        return clientTable.scan().stream().flatMap(p -> p.items().stream())
                .filter(c -> c.getNombre()!=null && c.getNombre().toLowerCase(Locale.ROOT).contains(q))
                .collect(Collectors.toList());
    }
}

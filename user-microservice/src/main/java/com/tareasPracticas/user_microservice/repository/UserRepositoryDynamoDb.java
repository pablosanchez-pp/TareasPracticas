package com.tareasPracticas.user_microservice.repository;

import com.tareasPracticas.user_microservice.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryDynamoDb implements UserRepository {

    private final DynamoDbTable<UserEntity> userTable;

    private static String pk(String id) { return "USER#" + id; }
    private static String sk(String id) { return "USER#" + id; }

    @Override
    public UserEntity save(UserEntity entity) {
            userTable.putItem(entity);
            return entity;
    }

    @Override
    public Optional<UserEntity> findById(String id) {
        Key key = Key.builder()
                .partitionValue(pk(id))
                .sortValue(sk(id))
                .build();

        return Optional.ofNullable(userTable.getItem(r -> r.key(key)));
    }

    // Normaliza username para búsquedas consistentes
    private static String normUsername(String s) {
        if (s == null) return null;
        return s.toLowerCase(Locale.ROOT).trim();
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        String norm = normUsername(username);
        if (norm == null || norm.isBlank()) return Optional.empty();

        try {
            DynamoDbIndex<UserEntity> gsi = userTable.index("GSI1");

            String partitionKey = "USERNAME#" + norm;

        List<UserEntity> results = gsi.query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(
                    Key.builder().partitionValue(partitionKey).build()
                )))
            .stream()
            .flatMap(p -> p.items().stream())
            .collect(Collectors.toList());

        if (!results.isEmpty()) {
        return Optional.of(results.get(0));
        }

        // If GSI query returned empty (maybe older records without GIndex1Pk), fall back to scanning
        List<UserEntity> scanned = userTable.scan().items().stream()
            .filter(u -> norm.equals(normUsername(u.getUsername())))
            .collect(Collectors.toList());

        if (scanned.isEmpty()) return Optional.empty();
        return Optional.of(scanned.get(0));

        } catch (Exception ex) {
            List<UserEntity> scanned = userTable.scan().items().stream()
                    .filter(u -> norm.equals(normUsername(u.getUsername())))
                    .collect(Collectors.toList());

            if (scanned.isEmpty()) return Optional.empty();
            return Optional.of(scanned.get(0));
        }
    }

    @Override
    public boolean logoutById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }

        return findById(id)
                .map(user -> {
                    user.setActive(false);
                    userTable.putItem(user);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public List<UserEntity> findAll() {
        return userTable.scan().items().stream().collect(Collectors.toList());
    }
}

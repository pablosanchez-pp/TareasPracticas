package com.tareasPracticas.user_microservice.service;


import com.tareasPracticas.user_microservice.entity.UserEntity;
import com.tareasPracticas.user_microservice.mapper.UserMapper;
import com.tareasPracticas.user_microservice.model.UserCreateRequest;
import com.tareasPracticas.user_microservice.model.UserLoginRequest;
import com.tareasPracticas.user_microservice.model.UserResponse;
import com.tareasPracticas.user_microservice.repository.UserRepositoryDynamoDb;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;


import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class UserService implements UserServ{

    private final UserRepositoryDynamoDb userRepositoryDynamoDb;
    private final UserMapper mapper;
    

    

    public UserResponse register(UserCreateRequest req) {
        // Verificar que no exista username
        if (userRepositoryDynamoDb.findByUsername(req.getUsername()).isPresent()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Username ya existe");
        }

        UserEntity userEntity = mapper.toEntity(req);

        if (userEntity.getId() == null) {
            userEntity.setId(UUID.randomUUID().toString());
        }

        userEntity.setPK("USER#" + userEntity.getId());
        userEntity.setSK("USER#" + userEntity.getId());

        // Setear GSI para búsquedas por username (normalizado)
        userEntity.setGIndex1Pk("USERNAME#" + normalizeUsername(req.getUsername()));

        // Metadatos
        userEntity.setCreatedDate(Instant.now());
        userEntity.setActive(true);

        UserEntity saved = userRepositoryDynamoDb.save(userEntity);
        UserResponse resp = mapper.toResponse(saved);
    // generate token including nombre and edad (use edad=18 for valid requests)
    int edad = 18;
    String token = generateToken(saved.getUsername(), edad);
    resp.setToken(token);
    return resp;
    }


    public UserResponse login(UserLoginRequest req) {
        UserEntity user = userRepositoryDynamoDb.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuario o contraseña inválidos"));

        if (!user.getPassword().equals(req.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Usuario o contraseña inválidos");
        }

        // Marcar como activo al iniciar sesión
        if (!user.isActive()) {
            user.setActive(true);
            userRepositoryDynamoDb.save(user);
        }

        UserResponse resp = toResponse(user);
    int edad = 18;
    String token = generateToken(user.getUsername(), edad);
    resp.setToken(token);
    return resp;
    }

    // Simple JWT-like token: base64url(header).base64url(payload).
    // AgeInterceptor only decodes the payload, so no signature is required here.
    private String generateToken(String username, int edad) {
        try {
            String header = "{\"alg\":\"none\",\"typ\":\"JWT\"}";
            String payload = String.format("{\"nombre\":\"%s\",\"edad\":%d}", username, edad);
            String h = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String p = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            // leave signature empty
            return String.format("%s.%s.", h, p);
        } catch (Exception ex) {
            return null;
        }
    }

    private UserResponse toResponse(UserEntity e) {
        UserResponse r = new UserResponse();
        r.setId(e.getId());
        r.setUsername(e.getUsername());
        r.setActive(e.isActive());
        return r;
    }

    public void logout(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "id is required");
        }

        boolean updated = userRepositoryDynamoDb.logoutById(id);
        if (!updated) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
    }

    public List<UserResponse> mostrarTodos() {
        return userRepositoryDynamoDb.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private String normalizeUsername(String username) {
        if (username == null) return null;
        String n = java.text.Normalizer.normalize(username, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return n.toLowerCase(Locale.ROOT).trim();
    }

}
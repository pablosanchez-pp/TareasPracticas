package com.tareasPracticas.client_microservice.repository;

import com.tareasPracticas.client_microservice.entity.ClientEntity;

import java.util.Optional;
import java.util.List;

public interface ClientRepository {
    ClientEntity save(ClientEntity entity);

    Optional<ClientEntity> findById(Long id);

    Optional<ClientEntity> findByEmail(String email);

    List<ClientEntity> findByName(String nameLike);
}

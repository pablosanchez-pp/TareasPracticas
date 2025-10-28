package com.tareasPracticas.client_microservice.service;

import com.tareasPracticas.client_microservice.entity.ClientEntity;
import com.tareasPracticas.client_microservice.exceptions.IdOnlyOut;
import com.tareasPracticas.client_microservice.exceptions.NotFoundException;
import com.tareasPracticas.client_microservice.mappers.ClientMapper;
import com.tareasPracticas.client_microservice.model.*;
import com.tareasPracticas.client_microservice.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;
    private final IdGenerator idGenerator = () -> UUID.randomUUID().toString();

    @Override
    public ClientOut create(ClientIn in) {
        ClientEntity entity = mapper.toEntity(in);

        if (entity.getId() == null)
            entity.setId(idGenerator.nextId());

        entity.setPK("CLIENT#" + entity.getId());
        entity.setSK("CLIENT#" + entity.getId());
        entity.setStatus("ACTIVE");
        entity.setCreatedDate(Instant.now());

        if (in.getEmail() != null) { entity.setGIndex2Pk("EMAIL#" + in.getEmail().toLowerCase(Locale.ROOT));}

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
        String q = in.getName();
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

        // Actualiza solo campos no nulos desde el DTO de entrada
        mapper.updateEntityFromIn(in, existing);

        if (in.getEmail() != null) {
            existing.setGIndex2Pk("EMAIL#" + in.getEmail().toLowerCase(Locale.ROOT));
        }

        ClientEntity saved = repository.save(existing);
        return mapper.toOut(saved);
    }

    interface IdGenerator {
        String nextId();
    }
}

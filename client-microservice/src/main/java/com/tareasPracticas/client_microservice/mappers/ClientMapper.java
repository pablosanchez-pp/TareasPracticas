package com.tareasPracticas.client_microservice.mappers;

import com.tareasPracticas.client_microservice.entity.ClientEntity;
import com.tareasPracticas.client_microservice.model.ClientIn;
import com.tareasPracticas.client_microservice.model.ClientOut;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientEntity toEntity(ClientIn in);
    ClientOut toEntity(ClientOut out);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromIn(ClientIn in, @MappingTarget ClientEntity target);

    @AfterMapping
    default void fillSystemFields(@MappingTarget ClientEntity e) {
        if (e.getId() == null) e.setId(java.util.UUID.randomUUID().toString());
        e.setPK("CLIENT#" + e.getId());
        e.setSK("META");
        if (e.getCreatedDate() == null) e.setCreatedDate(java.time.Instant.now());
        if (e.getStatus() == null) e.setStatus("ACTIVE");

        // Índice secundario: prioriza email, si no hay, nombre
        if (e.getEmail() != null && !e.getEmail().isEmpty()) {
            e.setGIndex2Pk("CLIENT#EMAIL#" + e.getEmail().toLowerCase());
        } else if (e.getName() != null && !e.getName().isEmpty()) {
            e.setGIndex2Pk("CLIENT#NAME#" + e.getName().toLowerCase());
        }
    }
}

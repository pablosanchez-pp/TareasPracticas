package com.tareasPracticas.client_microservice.mappers;

import com.tareasPracticas.client_microservice.entity.ClientEntity;
import com.tareasPracticas.client_microservice.model.ClientIn;
import com.tareasPracticas.client_microservice.model.ClientOut;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ClientMapper {

    // ===== CREATE: In -> Entity =====
    @Mappings({
            // Campos técnicos los rellenas en el servicio
            @Mapping(target = "PK", ignore = true),
            @Mapping(target = "SK", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "GIndex2Pk", ignore = true),
            @Mapping(target = "createdDate", ignore = true),

            // keyWordSearch lo calcula el servicio (no el mapper)
            @Mapping(target = "keyWordSearch", ignore = true)
    })
    ClientEntity toEntity(ClientIn in);

    // ===== READ: Entity -> Out =====
    ClientOut toOut(ClientEntity entity);

    // ===== UPDATE: in-place =====
    @Mappings({
            // Campos técnicos no se tocan aquí
            @Mapping(target = "PK", ignore = true),
            @Mapping(target = "SK", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "GIndex2Pk", ignore = true),
            @Mapping(target = "createdDate", ignore = true),

            // keyWordSearch lo recalcula el servicio si procede
            @Mapping(target = "keyWordSearch", ignore = true)
    })
    void updateEntityFromIn(ClientIn in, @MappingTarget ClientEntity target);
}

package com.tareasPracticas.client_microservice.mappers;

import com.tareasPracticas.client_microservice.entity.ClientEntity;
import com.tareasPracticas.client_microservice.model.ClientIn;
import com.tareasPracticas.client_microservice.model.ClientOut;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ClientMapper {

    @Mappings({
            @Mapping(target = "PK", ignore = true),
            @Mapping(target = "SK", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "GIndex2Pk", ignore = true),
            @Mapping(target = "createdDate", ignore = true)
    })
    ClientEntity toEntity(ClientIn in);

    ClientOut toOut(ClientEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "PK", ignore = true),
            @Mapping(target = "SK", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "GIndex2Pk", ignore = true),
            @Mapping(target = "createdDate", ignore = true)
    })
    void updateEntityFromIn(ClientIn in, @MappingTarget ClientEntity target);
}


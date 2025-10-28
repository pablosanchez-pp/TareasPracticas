package com.tareasPracticas.client_microservice.mappers;

import com.tareasPracticas.client_microservice.entity.ClientEntity;
import com.tareasPracticas.client_microservice.model.ClientIn;
import com.tareasPracticas.client_microservice.model.ClientOut;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientEntity toEntity(ClientIn in);
    ClientOut toOut(ClientEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromIn(ClientIn in, @MappingTarget ClientEntity target);
}

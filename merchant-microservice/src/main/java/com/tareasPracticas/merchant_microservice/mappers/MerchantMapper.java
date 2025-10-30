package com.tareasPracticas.merchant_microservice.mappers;

import com.tareasPracticas.merchant_microservice.entity.MerchantEntity;
import com.tareasPracticas.merchant_microservice.model.MerchantIn;
import com.tareasPracticas.merchant_microservice.model.MerchantOut;
import com.tareasPracticas.merchant_microservice.model.MerchantSimpleOut;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MerchantMapper {

    @Mappings({
            @Mapping(target = "PK", ignore = true),
            @Mapping(target = "SK", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "GIndex2Pk", ignore = true),
            @Mapping(target = "createdDate", ignore = true)
    })
    MerchantEntity toEntity(MerchantIn in);

    MerchantOut toOut(MerchantEntity entity);

    @Mapping(target = "id", source = "id")
    MerchantSimpleOut toSimpleOut(MerchantEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "PK", ignore = true),
            @Mapping(target = "SK", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "gIndex2Pk", ignore = true),
            @Mapping(target = "createdDate", ignore = true)
    })
    void updateEntityFromIn(MerchantIn in, @MappingTarget MerchantEntity target);
}

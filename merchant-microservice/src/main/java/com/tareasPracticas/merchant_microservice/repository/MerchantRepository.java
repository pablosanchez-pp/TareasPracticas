package com.tareasPracticas.merchant_microservice.repository;

import com.tareasPracticas.merchant_microservice.entity.MerchantEntity;

import java.util.List;
import java.util.Optional;

public interface MerchantRepository {
    MerchantEntity save(MerchantEntity entity);

    List<MerchantEntity> findByNameContainingIgnoreCase(String nombre);

    Optional<MerchantEntity> findById(String id);
}

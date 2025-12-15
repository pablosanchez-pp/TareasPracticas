package com.tareasPracticas.merchant_microservice.service;

import com.tareasPracticas.merchant_microservice.model.MerchantIn;
import com.tareasPracticas.merchant_microservice.model.MerchantOut;
import com.tareasPracticas.merchant_microservice.model.MerchantSimpleOut;

import java.util.List;
import java.util.Optional;

public interface MerchantService {
    MerchantOut create(MerchantIn in);
    MerchantOut findById(String id);
    MerchantSimpleOut findSimpleById(String id);
    List<MerchantOut> findByName(String name);
    MerchantOut update(String id, MerchantIn in);
    List<String> findClientIdsOfMerchant(String merchantId);
    List<MerchantOut> findAll();

    void delete(String id);
}

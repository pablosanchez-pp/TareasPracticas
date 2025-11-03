package com.tareasPracticas.client_microservice.service;

import com.tareasPracticas.client_microservice.feign.MerchantClient;
import com.tareasPracticas.client_microservice.model.ExistsOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantCheckService {

    private final MerchantClient merchantClient;

    public ExistsOut merchantExists(String merchantId, String jwt){
        try {
            merchantClient.findById(merchantId,jwt);
            return new ExistsOut(true);
        } catch (feign.FeignException e) {
            return new ExistsOut(false);
        }
    }
}

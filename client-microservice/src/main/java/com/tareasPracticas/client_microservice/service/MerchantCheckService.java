package com.tareasPracticas.client_microservice.service;

import com.tareasPracticas.client_microservice.feign.MerchantClient;
import com.tareasPracticas.client_microservice.model.ExistsOut;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantCheckService {

    private final MerchantClient merchantClient;

    public ExistsOut merchantExists(String merchantId){
        try{
            MerchantClient.MerchantOut out = merchantClient.findById(merchantId);
            return new ExistsOut(out != null && out.getId() != null);
        } catch (FeignException.NotFound e){
            return new ExistsOut(false);
        }
    }
}

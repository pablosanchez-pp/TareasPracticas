package com.tareasPracticas.client_microservice.controller;

import com.tareasPracticas.client_microservice.model.ExistsOut;
import com.tareasPracticas.client_microservice.service.MerchantCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientMerchantController {
    private final MerchantCheckService merchantCheckService;

    @GetMapping("/merchant-exists/{merchantId}")
    public ExistsOut merchantExists(@PathVariable String merchantId) {
        return merchantCheckService.merchantExists(merchantId);
    }
}

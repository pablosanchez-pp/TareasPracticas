package com.tareasPracticas.client_microservice.controller;

import com.tareasPracticas.client_microservice.model.ExistsOut;
import com.tareasPracticas.client_microservice.service.MerchantCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientMerchantController {
    private final MerchantCheckService merchantCheckService;

    @GetMapping("/merchant-exists/{merchantId}")
    public ExistsOut merchantExists(@PathVariable String merchantId) {
        return merchantCheckService.merchantExists(merchantId);
    }

    @GetMapping("/{clientId}/merchants/{merchantId}/exists")
    public ExistsOut exists(@PathVariable String clientId,
                            @PathVariable String merchantId) {
        return merchantCheckService.merchantExists(merchantId);
    }
}

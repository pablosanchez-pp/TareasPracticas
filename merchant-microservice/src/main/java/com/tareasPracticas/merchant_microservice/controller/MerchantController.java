package com.tareasPracticas.merchant_microservice.controller;

import com.tareasPracticas.merchant_microservice.model.MerchantIn;
import com.tareasPracticas.merchant_microservice.model.MerchantOut;
import com.tareasPracticas.merchant_microservice.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
@Validated
public class MerchantController {
    private final MerchantService service;

    @PostMapping
    public MerchantOut create(@Valid @RequestBody MerchantIn in) {
        return service.create(in);
    }

    @GetMapping("/{id}")
    public Object findById(
            @PathVariable String id,
            @RequestParam(name = "simpleOutput", required = false, defaultValue = "false") boolean simpleOutput) {
        return simpleOutput ? service.findSimpleById(id) : service.findById(id);
    }

    @GetMapping("/nombre/{nombre}")
    public List<MerchantOut> findByNombre(@PathVariable String nombre) {
        return service.findByName(nombre);
    }

    @PutMapping("/{id}")
    public MerchantOut update(@PathVariable String id,@RequestBody MerchantIn in) {
        return service.update(id, in);
    }

    @GetMapping("/{merchantId}/client")
    public ResponseEntity<?> getClientOfMerchant(@PathVariable String merchantId) {
        Optional<String> clientIdOpt = service.findClientIdOfMerchant(merchantId);

        return clientIdOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Este merchant no tiene cliente asociado"));
    }
}

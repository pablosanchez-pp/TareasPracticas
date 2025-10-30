package com.tareasPracticas.merchant_microservice.controller;

import com.tareasPracticas.merchant_microservice.model.MerchantIn;
import com.tareasPracticas.merchant_microservice.model.MerchantOut;
import com.tareasPracticas.merchant_microservice.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public MerchantOut update(@PathVariable String id, @Valid @RequestBody MerchantIn in) {
        return service.update(id, in);
    }
}

package com.tareasPracticas.client_microservice.controller;

import com.tareasPracticas.client_microservice.entity.ClientEntity;
import com.tareasPracticas.client_microservice.model.*;
import com.tareasPracticas.client_microservice.repository.ClientRepository;
import com.tareasPracticas.client_microservice.service.ClientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@Validated
public class ClientController {
    private final ClientService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientOut create(@Valid @RequestBody ClientIn in, @RequestParam("jwt")  String jwt) {
        return service.create(in);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(
            @PathVariable String id,
            @RequestParam(value = "simpleOutput", required = false) Boolean simpleOutput,
            @RequestParam("jwt") String jwt) {
        return ResponseEntity.ok(service.findById(new ClientByIdIn(id, simpleOutput)));
    }

    @GetMapping("/search/by-name")
    public List<ClientOut> findByName(@RequestParam("query") String nameLike,
                                      @RequestParam("jwt") String jwt) {
        return service.findByName(new ClientByNameIn(nameLike));
    }

    @GetMapping("/search/by-email")
    public ClientOut findByEmail(
            @RequestParam("email")
            @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                    message = "formato de email inválido") String email,
            @RequestParam("jwt") String jwt) {
        return service.findByEmail(new ClientByEmailIn(email));
    }

    @PutMapping("/{id}")
    public ClientOut update(@PathVariable String id, @RequestBody ClientIn in,
                            @RequestParam("jwt") String jwt) {
        return service.update(id, in);
    }

    @PostMapping("/{clientId}/merchants/{merchantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void link(@PathVariable String clientId, @PathVariable String merchantId,
                     @RequestParam("jwt") String jwt) {
        service.linkClientToMerchant(clientId, merchantId, jwt);
    }

    @GetMapping("/{clientId}/merchants")
    public List<String> listMerchants(@PathVariable String clientId,
                                      @RequestParam("jwt") String jwt) {
        return service.listMerchantIdsOfClient(clientId);
    }
}

package com.tareasPracticas.merchant_microservice.controller;

import com.tareasPracticas.merchant_microservice.model.MerchantIn;
import com.tareasPracticas.merchant_microservice.model.MerchantOut;
import com.tareasPracticas.merchant_microservice.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
@Validated
@Tag(name = "Merchants", description = "Operaciones CRUD y relaciones de merchants")
@SecurityRequirement(name = "bearerAuth") // o "jwtHeader" si tu esquema de seguridad se llama así
public class MerchantController {

    private final MerchantService service;

    @Operation(
            summary = "Crear merchant",
            description = "Crea un nuevo merchant a partir del modelo de entrada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Merchant creado correctamente",
                    content = @Content(schema = @Schema(implementation = MerchantOut.class))),
            @ApiResponse(responseCode = "400", description = "Petición inválida",
                    content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MerchantOut create(
            @Valid
            @RequestBody(
                    description = "Modelo de entrada del merchant",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MerchantIn.class))
            )
            @org.springframework.web.bind.annotation.RequestBody MerchantIn in
    ) {
        return service.create(in);
    }

    @Operation(summary = "Buscar merchant por ID",
            description = "Devuelve un merchant completo o simple según el parámetro 'simpleOutput'.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Merchant encontrado",
                    content = @Content(schema = @Schema(implementation = MerchantOut.class))),
            @ApiResponse(responseCode = "404", description = "Merchant no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public Object findById(
            @Parameter(description = "ID del merchant") @PathVariable String id,
            @Parameter(description = "Si es true devuelve salida simple (solo id)")
            @RequestParam(name = "simpleOutput", required = false, defaultValue = "false") boolean simpleOutput
    ) {
        return simpleOutput ? service.findSimpleById(id) : service.findById(id);
    }

    @Operation(summary = "Buscar merchants por nombre",
            description = "Devuelve todos los merchants cuyo nombre contenga el texto indicado (ignorando mayúsculas/minúsculas).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de merchants coincidentes",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MerchantOut.class))))
    })
    @GetMapping("/nombre/{nombre}")
    public List<MerchantOut> findByNombre(
            @Parameter(description = "Texto a buscar en el nombre del merchant") @PathVariable String nombre
    ) {
        return service.findByName(nombre);
    }

    @Operation(
            summary = "Actualizar merchant",
            description = "Actualiza los datos de un merchant existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Merchant actualizado",
                    content = @Content(schema = @Schema(implementation = MerchantOut.class))),
            @ApiResponse(responseCode = "404", description = "Merchant no encontrado",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public MerchantOut update(
            @Parameter(description = "ID del merchant a actualizar")
            @PathVariable String id,
            @RequestBody(
                    description = "Datos nuevos del merchant",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MerchantIn.class))
            )
            @org.springframework.web.bind.annotation.RequestBody MerchantIn in
    ) {
        return service.update(id, in);
    }

    @Operation(summary = "Obtener cliente asociado a un merchant",
            description = "Devuelve el ID del cliente vinculado al merchant, si existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "El merchant no tiene cliente asociado", content = @Content)
    })
    @GetMapping("/{merchantId}/client")
    public ResponseEntity<?> getClientOfMerchant(
            @Parameter(description = "ID del merchant") @PathVariable String merchantId
    ) {
        Optional<String> clientIdOpt = service.findClientIdOfMerchant(merchantId);
        return clientIdOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Este merchant no tiene cliente asociado"));
    }
}

package com.tareasPracticas.client_microservice.controller;

import com.tareasPracticas.client_microservice.model.ExistsOut;
import com.tareasPracticas.client_microservice.service.MerchantCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@Tag(name = "Clientes - Chequeo de Merchants", description = "Endpoints para comprobar existencia de merchants y relaciones")
@SecurityRequirement(name = "jwtHeader")
public class ClientMerchantController {

    private final MerchantCheckService merchantCheckService;

    @Operation(
            summary = "Comprobar si existe un merchant por ID",
            description = "Devuelve true/false en función de si el merchant existe en el microservicio de Merchant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado de la comprobación",
                    content = @Content(schema = @Schema(implementation = ExistsOut.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content)
    })
    @GetMapping("/merchant-exists/{merchantId}")
    public ExistsOut merchantExists(
            @Parameter(description = "ID del merchant a comprobar", required = true)
            @PathVariable String merchantId
    ) {
        return merchantCheckService.merchantExists(merchantId);
    }

    @Operation(
            summary = "Comprobar si un merchant asociado a un cliente existe",
            description = "No valida la relación cliente-merchant, únicamente comprueba la existencia del merchant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado de la comprobación",
                    content = @Content(schema = @Schema(implementation = ExistsOut.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content)
    })
    @GetMapping("/{clientId}/merchants/{merchantId}/exists")
    public ExistsOut exists(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String clientId,
            @Parameter(description = "ID del merchant a comprobar", required = true)
            @PathVariable String merchantId
    ) {
        return merchantCheckService.merchantExists(merchantId);
    }
}


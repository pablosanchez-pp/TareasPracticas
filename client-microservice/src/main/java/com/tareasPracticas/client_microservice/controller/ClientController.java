package com.tareasPracticas.client_microservice.controller;

import com.tareasPracticas.client_microservice.model.*;
import com.tareasPracticas.client_microservice.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@Validated
@Tag(name = "Clientes", description = "Operaciones CRUD y relaciones de clientes")
@SecurityRequirement(name = "jwtHeader")
public class ClientController {

    private final ClientService service;

    @Operation(
            summary = "Crear cliente",
            description = "Crea un nuevo cliente a partir del modelo de entrada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado",
                    content = @Content(schema = @Schema(implementation = ClientOut.class))),
            @ApiResponse(responseCode = "400", description = "Petición inválida",
                    content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientOut create(
            @Valid
            @RequestBody(
                    description = "Modelo de entrada del cliente",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ClientIn.class))
            )
            @org.springframework.web.bind.annotation.RequestBody ClientIn in
    ) {
        return service.create(in);
    }

    @Operation(
            summary = "Obtener cliente por ID",
            description = "Devuelve el cliente por su identificador. Con `simpleOutput=true` devuelve salida simple."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(schema = @Schema(implementation = ClientOut.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(
            @Parameter(description = "ID del cliente", required = true, example = "d9c0a775-8ef1-4bc4-adcf-cb21753371a8")
            @PathVariable String id,
            @Parameter(description = "Si es true devuelve salida simple (solo id)", example = "false")
            @RequestParam(value = "simpleOutput", required = false) Boolean simpleOutput
    ) {
        return ResponseEntity.ok(service.findById(new ClientByIdIn(id, simpleOutput)));
    }

    @Operation(
            summary = "Buscar clientes por nombre",
            description = "Búsqueda 'like' por nombre. Devuelve una lista de clientes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de clientes",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClientOut.class))))
    })
    @GetMapping("/findByName")
    public List<ClientOut> findByName(@RequestParam String query) {
        String q = query == null ? "" : query.trim();
        if (q.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "query must not be empty");
        }
        return service.findByName(new ClientByNameIn(q));
    }

    @Operation(
            summary = "Buscar cliente por email",
            description = "Devuelve un único cliente a partir de su email."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(schema = @Schema(implementation = ClientOut.class))),
            @ApiResponse(responseCode = "400", description = "Email con formato inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @GetMapping("/search/by-email")
    public ClientOut findByEmail(
            @Parameter(description = "Email del cliente", example = "pablo@example.com")
            @RequestParam("email")
            @Pattern(
                    regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                    message = "formato de email inválido"
            ) String email
    ) {
        return service.findByEmail(new ClientByEmailIn(email));
    }

    @Operation(
            summary = "Actualizar cliente",
            description = "Actualiza los datos del cliente indicado por ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado",
                    content = @Content(schema = @Schema(implementation = ClientOut.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ClientOut update(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String id,
            @RequestBody(
                    description = "Modelo de entrada con cambios",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ClientIn.class))
            )
            @org.springframework.web.bind.annotation.RequestBody ClientIn in
    ) {
        return service.update(id, in);
    }

    @Operation(
            summary = "Vincular cliente con merchant",
            description = "Crea la relación entre un cliente y un merchant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vinculación creada"),
            @ApiResponse(responseCode = "404", description = "Cliente o merchant no encontrados", content = @Content)
    })
    @PostMapping("/{clientId}/merchants/{merchantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void link(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String clientId,
            @Parameter(description = "ID del merchant", required = true)
            @PathVariable String merchantId
    ) {
        service.linkClientToMerchant(clientId, merchantId);
    }

    @Operation(
            summary = "Listar merchants de un cliente",
            description = "Devuelve la lista de IDs de merchants asociados al cliente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de IDs de merchants",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    @GetMapping("/{clientId}/merchants")
    public List<String> listMerchants(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String clientId
    ) {
        return service.listMerchantIdsOfClient(clientId);
    }

    @GetMapping("/findAll")
    public List<ClientOut> findAll() {
        return service.findAll();
    }


    @Operation(
            summary = "Borrar cliente",
            description = "Elimina el cliente indicado por su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente borrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String id
    ) {
        // Aquí delegas al servicio
        service.delete(id);
    }
}

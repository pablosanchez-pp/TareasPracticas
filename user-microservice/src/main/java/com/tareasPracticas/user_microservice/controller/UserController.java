package com.tareasPracticas.user_microservice.controller;

import com.tareasPracticas.user_microservice.model.UserCreateRequest;
import com.tareasPracticas.user_microservice.model.UserLoginRequest;
import com.tareasPracticas.user_microservice.model.UserResponse;
import com.tareasPracticas.user_microservice.service.UserService;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, login y logout de usuarios")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Registrar usuario",
            description = "Crea un nuevo usuario con username y password."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario creado correctamente",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o username duplicado",
                    content = @Content
            )
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de registro",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserCreateRequest.class))
            )
            @RequestBody UserCreateRequest req
    ) {
        UserResponse created = userService.register(req);
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "Login de usuario",
            description = "Autentica al usuario mediante username y password."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login correcto",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario o contraseña inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario inactivo",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales de login",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserLoginRequest.class))
            )
            @RequestBody UserLoginRequest req,
            HttpSession session
    ) {
        UserResponse user = userService.login(req);

        if (session != null) {
            session.setAttribute("USER_ID", user.getId());
            session.setAttribute("USERNAME", user.getUsername());
        }

        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Logout de usuario",
            description = "Cierra la sesión del usuario y lo marca como inactivo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout realizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content
            )
    })
    @PostMapping("/logout/{id}")
    public ResponseEntity<Void> logout(
            @Parameter(
                    description = "ID del usuario",
                    required = true,
                    example = "d9c0a775-8ef1-4bc4-adcf-cb21753371a8"
            )
            @PathVariable String id,
            HttpSession session
    ) {
        userService.logout(id);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }

        @Operation(
                        summary = "Listar usuarios",
                        description = "Devuelve la lista de todos los usuarios"
        )
        @GetMapping("/usuarios")
        public ResponseEntity<List<UserResponse>> mostrarTodos() {
                List<UserResponse> users = userService.mostrarTodos();
                return ResponseEntity.ok(users);
        }
}

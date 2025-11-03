package com.tareasPracticas.merchant_microservice.config.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AgeInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String jwt = request.getParameter("jwt");
        if (jwt == null || jwt.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta el parámetro 'jwt' en la petición");
        }

        int edad = extractEdad(jwt);
        if (edad < 18) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado: edad mínima 18");
        }
        return true;
    }

    private int extractEdad(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("JWT inválido");
            String payloadB64 = parts[1];

            String normalized = payloadB64.replace('-', '+').replace('_', '/');
            switch (normalized.length() % 4) {
                case 2: normalized += "=="; break;
                case 3: normalized += "="; break;
                default: break;
            }

            String json = new String(Base64.getDecoder().decode(normalized), StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(json);

            JsonNode edadNode = node.get("edad");
            if (edadNode == null || !edadNode.isInt()) {
                throw new IllegalArgumentException("El payload no contiene 'edad' válida");
            }
            return edadNode.asInt();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT inválido: " + e.getMessage());
        }
    }

}

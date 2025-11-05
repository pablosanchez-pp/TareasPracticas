package com.tareasPracticas.client_microservice.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("Client Microservice API")
                    .version("v1")
                    .description("Documentación de endpoints del microservicio de Cliente"))
                .components(new Components().addSecuritySchemes(
                        "jwtHeader",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("jwt")
                ))
                .addSecurityItem(new SecurityRequirement().addList("jwtHeader"));
    }
}

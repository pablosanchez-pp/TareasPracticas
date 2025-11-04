package com.tareasPracticas.client_microservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeingAuthConfig {
    @Bean
    public RequestInterceptor authInterceptor() {
        return template -> {
            var attrs = RequestContextHolder.getRequestAttributes();
            if(attrs instanceof ServletRequestAttributes sra){
                String auth = sra.getRequest().getHeader("Authorization");
                if( auth != null) template.header("Authorization", auth);
            }
        };
    }
}

package com.tareasPracticas.user_microservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLogoutRequest {
    @NotBlank(message = "id is required")
    private String id;
}


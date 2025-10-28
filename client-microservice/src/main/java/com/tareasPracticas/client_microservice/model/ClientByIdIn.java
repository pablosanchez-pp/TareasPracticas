package com.tareasPracticas.client_microservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientByIdIn {
    @NotBlank private String id;
    private Boolean simpleOutPut;
}

package com.tareasPracticas.client_microservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExistsOut {
    private boolean exists;
}

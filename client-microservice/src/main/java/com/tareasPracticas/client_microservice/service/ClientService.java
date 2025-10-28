package com.tareasPracticas.client_microservice.service;

import com.tareasPracticas.client_microservice.model.*;

import java.util.List;

public interface ClientService {
    ClientOut create(ClientIn in);

    Object findById(ClientByIdIn in);

    List<ClientOut> findByName(ClientByNameIn in);

    ClientOut findByEmail(ClientByEmailIn in);

    ClientOut update(String id, ClientIn in);

}

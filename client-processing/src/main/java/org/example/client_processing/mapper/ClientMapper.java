package org.example.client_processing.mapper;

import org.example.client_processing.dto.RegistrationRequest;
import org.example.client_processing.model.Client;
import org.example.client_processing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientProducts", ignore = true)
    @Mapping(target = "user", source = "user")
    Client toEntity(RegistrationRequest.ClientPart clientPart, User user);
}

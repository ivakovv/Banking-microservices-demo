package org.example.client_processing.mapper;

import org.example.client_processing.dto.client.ClientDto;
import org.example.client_processing.dto.client.RegistrationRequest;
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

    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "middleName", target = "middleName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "documentType", target = "documentType")
    @Mapping(source = "documentId", target = "documentId")
    @Mapping(source = "documentPrefix", target = "documentPrefix")
    @Mapping(source = "documentSuffix", target = "documentSuffix")
    ClientDto toResponse(Client client);
}

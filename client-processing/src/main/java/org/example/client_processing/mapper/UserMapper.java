package org.example.client_processing.mapper;

import org.example.client_processing.dto.RegistrationRequest;
import org.example.client_processing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    User toEntity(RegistrationRequest.UserPart userPart);
}

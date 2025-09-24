package org.example.client_processing.mapper;

import org.example.client_processing.dto.BlacklistRegistryRequest;
import org.example.client_processing.dto.BlacklistRegistryResponse;
import org.example.client_processing.model.BlacklistRegistry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlacklistRegistryMapper {
    
    @Mapping(target = "blacklistedAt", ignore = true) 
    BlacklistRegistry toEntity(BlacklistRegistryRequest request);
    
    BlacklistRegistryResponse toResponse(BlacklistRegistry blacklistRegistry);
}

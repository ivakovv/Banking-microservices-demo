package org.example.client_processing.mapper;

import org.example.client_processing.dto.blacklist.BlacklistRegistryRequest;
import org.example.client_processing.dto.blacklist.BlacklistRegistryResponse;
import org.example.client_processing.model.BlacklistRegistry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlacklistRegistryMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "blacklistedAt", ignore = true) 
    BlacklistRegistry toEntity(BlacklistRegistryRequest request);
    
    BlacklistRegistryResponse toResponse(BlacklistRegistry blacklistRegistry);
}

package org.example.account_processing.mapper;

import org.example.account_processing.dto.card.ClientCardEventDto;
import org.example.account_processing.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "cardId", ignore = true)
    @Mapping(target = "status", ignore = true)
    Card toEntity(ClientCardEventDto eventDto);
    
}

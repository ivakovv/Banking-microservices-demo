package org.example.client_processing.mapper;

import org.example.client_processing.dto.card.ClientCardEventDto;
import org.example.client_processing.dto.client_product.ReleaseCardRequest;
import org.example.client_processing.dto.client_product.ReleaseCardResponse;
import org.example.client_processing.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CardMapper {

    default ReleaseCardResponse toResponse(String message) {
        return new ReleaseCardResponse(message, LocalDateTime.now());
    }

    @Mapping(target = "clientId", source = "client.clientId")
    @Mapping(target = "accountId", source = "request.accountId")
    @Mapping(target = "paymentSystem", source = "request.paymentSystem")
    @Mapping(target = "timestamp", ignore = true) 
    @Mapping(target = "eventType", ignore = true)
    ClientCardEventDto toClientCardEvent(Client client, ReleaseCardRequest request);
}

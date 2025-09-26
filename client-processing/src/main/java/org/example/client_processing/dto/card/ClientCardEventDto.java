package org.example.client_processing.dto.card;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ClientCardEventDto(
        String clientId,
        Long accountId,
        String paymentSystem,
        LocalDateTime timestamp,
        String eventType
) {
    public ClientCardEventDto {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (eventType == null) {
            eventType = "CLIENT_CARD_CREATED";
        }
    }
}

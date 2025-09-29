package org.example.account_processing.dto.card;

import java.time.LocalDateTime;

public record ClientCardEventDto(
        String clientId,
        Long accountId,
        String paymentSystem,
        LocalDateTime timestamp,
        String eventType
) {
}

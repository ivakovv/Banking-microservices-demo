package org.example.account_processing.dto.product;

import java.time.LocalDateTime;

public record ClientProductEventDto(
        Long clientProductId,
        String clientId,
        String productId,
        String productName,
        String productType,
        String eventType,
        LocalDateTime openDate,
        LocalDateTime closeDate,
        String status,
        LocalDateTime timestamp,
        String description
) {
}

package org.example.credit_processing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClientCreditProductEventDto(
        Long clientProductId,
        String clientId,
        String productId,
        String productName,
        String productType,
        String eventType,
        BigDecimal creditAmount,
        BigDecimal interestRate,
        LocalDateTime openDate,
        LocalDateTime closeDate,
        String status,
        LocalDateTime timestamp,
        String description
) {
}
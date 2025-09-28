package org.example.client_processing.dto.client_product;

import org.example.client_processing.enums.client_product.Status;
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
        Status status,
        LocalDateTime timestamp,
        String description
) {
}

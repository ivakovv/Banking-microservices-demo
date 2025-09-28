package org.example.client_processing.dto.client_product;

import org.example.client_processing.enums.client_product.Status;

import java.time.LocalDateTime;

public record ClientProductResponse(
        Long id,
        String clientId,
        String productId,
        String productName,
        String productType,
        LocalDateTime openDate,
        LocalDateTime closeDate,
        Status status
) {}

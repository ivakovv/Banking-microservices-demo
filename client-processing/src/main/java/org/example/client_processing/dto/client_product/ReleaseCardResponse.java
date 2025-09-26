package org.example.client_processing.dto.client_product;

import java.time.LocalDateTime;

public record ReleaseCardResponse(
        String message,
        LocalDateTime createdAt
) {
}

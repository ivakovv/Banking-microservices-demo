package org.example.client_processing.dto.client_product;

import org.example.client_processing.enums.client_product.Status;

import java.time.LocalDateTime;

public record ClientProductRequest(
        LocalDateTime openDate,
        LocalDateTime closeDate,
        Status status
) {
    public ClientProductRequest {
        if (openDate == null) {
            openDate = LocalDateTime.now();
        }
        if (status == null) {
            status = Status.ACTIVE;
        }
    }
}

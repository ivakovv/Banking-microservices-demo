package org.example.client_processing.dto.client_product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReleaseCardRequest(
        @NotNull
        Long accountId,
        @NotBlank
        String paymentSystem
) {
}

package org.example.client_processing.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.client_processing.enums.product.Key;

public record ProductRequest(
        @NotBlank
        String name,
        @NotNull
        Key key
) {}

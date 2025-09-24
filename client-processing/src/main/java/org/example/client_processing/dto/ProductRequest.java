package org.example.client_processing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.client_processing.enums.product.Key;

public record ProductRequest(
        @NotBlank(message = "The product name cannot be empty")
        String name,
        @NotNull(message = "The product type is required to fill in")
        Key key
) {}

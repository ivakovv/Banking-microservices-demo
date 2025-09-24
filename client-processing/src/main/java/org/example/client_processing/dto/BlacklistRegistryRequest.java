package org.example.client_processing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.client_processing.enums.client.DocumentType;
import java.time.LocalDateTime;

public record BlacklistRegistryRequest(
        @NotNull
        DocumentType documentType,
        
        @NotBlank
        String documentId,
        
        @NotBlank
        String reason,
        
        LocalDateTime blacklistExpirationDate,
        
        LocalDateTime blacklistedAt
) {}

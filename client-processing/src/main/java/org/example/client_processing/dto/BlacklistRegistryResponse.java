package org.example.client_processing.dto;

import org.example.client_processing.enums.client.DocumentType;
import java.time.LocalDateTime;

public record BlacklistRegistryResponse(
        DocumentType documentType,
        String documentId,
        LocalDateTime blacklistedAt,
        String reason,
        LocalDateTime blacklistExpirationDate
) {}

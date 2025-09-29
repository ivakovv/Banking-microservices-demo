package org.example.client_processing.dto.client;

import org.example.client_processing.enums.client.DocumentType;

import java.time.LocalDate;

public record ClientDto(
        String clientId,
        String firstName,
        String middleName,
        String lastName,
        LocalDate dateOfBirth,
        DocumentType documentType,
        String documentId,
        String documentPrefix,
        String documentSuffix
) {
}

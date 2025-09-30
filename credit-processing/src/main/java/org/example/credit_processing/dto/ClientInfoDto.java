package org.example.credit_processing.dto;

import java.time.LocalDate;

public record ClientInfoDto(
        String clientId,
        String firstName,
        String middleName,
        String lastName,
        LocalDate dateOfBirth,
        String documentType,
        String documentId,
        String documentPrefix,
        String documentSuffix
) {
}

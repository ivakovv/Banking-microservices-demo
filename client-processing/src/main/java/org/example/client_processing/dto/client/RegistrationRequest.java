package org.example.client_processing.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.example.client_processing.enums.client.DocumentType;

import java.time.LocalDate;

public record RegistrationRequest(
        @NotNull UserPart user,
        @NotNull ClientPart client
) {
    public record UserPart(
            @NotBlank String login,
            @NotBlank String password,
            @NotBlank @Email String email
    ) {}

    public record ClientPart(
            @NotBlank String clientId,
            @NotBlank String firstName,
            String middleName,
            @NotBlank String lastName,
            @NotNull @Past LocalDate dateOfBirth,
            @NotNull DocumentType documentType,
            @NotBlank String documentId,
            String documentPrefix,
            String documentSuffix
    ) {}
}



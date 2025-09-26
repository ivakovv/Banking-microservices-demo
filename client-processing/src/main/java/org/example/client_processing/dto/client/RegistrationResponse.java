package org.example.client_processing.dto.client;

public record RegistrationResponse(
        Long userId,
        String clientId,
        String login,
        String email
) {}



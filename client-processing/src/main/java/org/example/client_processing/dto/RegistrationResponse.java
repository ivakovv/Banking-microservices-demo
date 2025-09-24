package org.example.client_processing.dto;

public record RegistrationResponse(
        Long userId,
        String clientId,
        String login,
        String email
) {}



package org.example.client_processing.dto.auth;

public record RefreshTokenResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        String message
) {}

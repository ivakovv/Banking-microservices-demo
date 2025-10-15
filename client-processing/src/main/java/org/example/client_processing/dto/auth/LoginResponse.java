package org.example.client_processing.dto.auth;

import org.example.client_processing.enums.roles.UserRole;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserRole role,
        String message
) {}

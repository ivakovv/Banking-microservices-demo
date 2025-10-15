package org.example.client_processing.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String login,
        
        @NotBlank
        String password
) {}

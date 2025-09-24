package org.example.client_processing.dto.error;

public record ErrorResponseDto(int status, String error, String message) {
}

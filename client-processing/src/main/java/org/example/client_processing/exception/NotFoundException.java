package org.example.client_processing.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entityName, Long id) {
        super("%s with id %d not found".formatted(entityName, id));
    }

    public NotFoundException(String message) {
        super(message);
    }
}
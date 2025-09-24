package org.example.client_processing.controller.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.client_processing.dto.error.ErrorResponseDto;
import org.example.client_processing.exception.NotFoundException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleResponseStatusException(ResponseStatusException e){
        ErrorResponseDto errorResponse = new ErrorResponseDto(e.getStatusCode().value(), e.getReason(), e.getMessage());
        return new ResponseEntity<>(errorResponse, e.getStatusCode());
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(NotFoundException ex) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleNotReadable(HttpMessageNotReadableException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid data format",
                ex.getMessage()
        );
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(
            Exception ex,
            HttpServletRequest request) {

        log.error("An unexpected error occurred at {}: {}",
                request.getRequestURI(),
                ex.getMessage(),
                ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred"
        );
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDto> handleRuntimeExceptions(RuntimeException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                ex.getMessage()
        );
    }


    private ResponseEntity<ErrorResponseDto> buildErrorResponse(
            HttpStatus status,
            String error,
            String message) {

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                status.value(),
                error,
                message
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}

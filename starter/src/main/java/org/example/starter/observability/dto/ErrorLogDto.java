package org.example.starter.observability.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.starter.observability.annotation.LogDatasourceError;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorLogDto(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        LocalDateTime timestamp,
        
        String serviceName,
        
        String methodSignature,
        
        String exceptionMessage,
        
        String stackTrace,
        
        List<String> methodParameters,
        
        LogDatasourceError.LogLevel logLevel,
        
        String description
) {
}

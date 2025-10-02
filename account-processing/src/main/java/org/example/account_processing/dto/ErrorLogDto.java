package org.example.account_processing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.account_processing.annotation.LogDatasourceError;

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

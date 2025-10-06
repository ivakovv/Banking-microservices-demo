package org.example.client_processing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record MetricLogDto(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        LocalDateTime timestamp,
        
        String serviceName,
        
        String methodSignature,
        
        String type,
        
        Long executionTimeMs,
        
        List<String> methodParameters,
        
        String description
) {
}

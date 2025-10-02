package org.example.account_processing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;


public record HttpRequestLogDto(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        LocalDateTime timestamp,
        
        String serviceName,
        
        String methodSignature,
        
        String httpMethod,
        
        String requestUri,
        
        List<String> requestParameters,
        
        String requestBody,
        
        String responseBody,
        
        String description
) {
}

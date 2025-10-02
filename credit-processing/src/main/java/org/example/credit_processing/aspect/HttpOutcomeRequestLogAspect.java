package org.example.credit_processing.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.credit_processing.annotation.HttpOutcomeRequestLog;
import org.example.credit_processing.dto.HttpRequestLogDto;
import org.example.credit_processing.kafka.HttpRequestLogProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class HttpOutcomeRequestLogAspect {

    private final HttpRequestLogProducer httpRequestLogProducer;

    @Value("${spring.application.name}")
    private String serviceName;

    @AfterReturning(pointcut = "@annotation(httpOutcomeRequestLog)", returning = "result")
    public void logHttpOutcome(JoinPoint joinPoint, HttpOutcomeRequestLog httpOutcomeRequestLog, Object result) {
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            String methodSignatureStr = buildMethodSignature(method);
            
            List<String> requestParameters = extractMethodParameters(joinPoint);
            
            
            String requestUri = extractRequestUri(httpOutcomeRequestLog, joinPoint);
            
            
            String requestBody = extractRequestBody(joinPoint);
            
            
            String responseBody = extractResponseBody(result);

            HttpRequestLogDto httpRequestLogDto = new HttpRequestLogDto(
                    LocalDateTime.now(),
                    serviceName,
                    methodSignatureStr,
                    httpOutcomeRequestLog.httpMethod(),
                    requestUri,
                    requestParameters,
                    requestBody,
                    responseBody,
                    httpOutcomeRequestLog.description()
            );

            
            try {
                httpRequestLogProducer.sendHttpRequestLog(httpRequestLogDto);
            } catch (Exception kafkaException) {
                log.warn("Failed to send HTTP request log to Kafka: {}", kafkaException.getMessage());
            }

            
            logToConsole(httpRequestLogDto);

        } catch (Exception aspectException) {
            log.error("Error in HttpOutcomeRequestLogAspect while processing successful request: {}", 
                    aspectException.getMessage(), aspectException);
        }
    }

    
    private String buildMethodSignature(Method method) {
        StringBuilder signature = new StringBuilder();
        
        signature.append(method.getDeclaringClass().getSimpleName());
        signature.append(".");
        signature.append(method.getName());
        signature.append("(");
        
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) {
                signature.append(", ");
            }
            signature.append(paramTypes[i].getSimpleName());
        }
        
        signature.append(")");
        signature.append(" : ");
        signature.append(method.getReturnType().getSimpleName());
        
        return signature.toString();
    }

    
    private List<String> extractMethodParameters(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return List.of();
        }
        
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return "null";
                    }
                    String argStr = arg.toString();
                    return argStr.length() > 200 ? argStr.substring(0, 200) + "..." : argStr;
                })
                .collect(Collectors.toList());
    }

    
    private String extractRequestUri(HttpOutcomeRequestLog annotation, JoinPoint joinPoint) {
        
        if (!annotation.uri().isEmpty()) {
            return annotation.uri();
        }
        
        Object[] args = joinPoint.getArgs();
        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof String) {
                    String strArg = (String) arg;
                    if (strArg.startsWith("/") || strArg.startsWith("http")) {
                        return strArg;
                    }
                }
            }
        }
        
        return "unknown-uri";
    }

   
    private String extractRequestBody(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }
        
        for (Object arg : args) {
            if (arg != null && 
                !arg.getClass().isPrimitive() && 
                !(arg instanceof String) && 
                !(arg instanceof Number) && 
                !(arg instanceof Boolean)) {
                
                String bodyStr = arg.toString();
                return bodyStr.length() > 500 ? bodyStr.substring(0, 500) + "..." : bodyStr;
            }
        }
        
        return "";
    }

    private String extractResponseBody(Object result) {
        if (result == null) {
            return "null";
        }
        
        String resultStr = result.toString();
        return resultStr.length() > 500 ? resultStr.substring(0, 500) + "..." : resultStr;
    }

   
    private void logToConsole(HttpRequestLogDto httpRequestLogDto) {
        String logMessage = String.format(
                "HTTP REQUEST [INFO] %s %s in %s | Parameters: %s | Response: %s | Description: %s",
                httpRequestLogDto.httpMethod(),
                httpRequestLogDto.requestUri(),
                httpRequestLogDto.methodSignature(),
                httpRequestLogDto.requestParameters(),
                httpRequestLogDto.responseBody(),
                httpRequestLogDto.description()
        );

        log.info(logMessage);
    }
}

package org.example.credit_processing.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.credit_processing.annotation.HttpIncomeRequestLog;
import org.example.credit_processing.dto.HttpIncomeRequestLogDto;
import org.example.credit_processing.kafka.HttpIncomeRequestLogProducer;
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
public class HttpIncomeRequestLogAspect {

    private final HttpIncomeRequestLogProducer httpIncomeRequestLogProducer;

    @Value("${spring.application.name}")
    private String serviceName;

    @Before("@annotation(httpIncomeRequestLog)")
    public void logHttpIncome(JoinPoint joinPoint, HttpIncomeRequestLog httpIncomeRequestLog) {
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            String methodSignatureStr = buildMethodSignature(method);
            
            List<String> requestParameters = extractMethodParameters(joinPoint);
            
            
            String requestUri = extractRequestUri(httpIncomeRequestLog, joinPoint);
            
            
            String requestBody = extractRequestBody(joinPoint);

            HttpIncomeRequestLogDto httpIncomeRequestLogDto = new HttpIncomeRequestLogDto(
                    LocalDateTime.now(),
                    serviceName,
                    methodSignatureStr,
                    httpIncomeRequestLog.httpMethod(),
                    requestUri,
                    requestParameters,
                    requestBody,
                    httpIncomeRequestLog.description()
            );

            
            try {
                httpIncomeRequestLogProducer.sendHttpIncomeRequestLog(httpIncomeRequestLogDto);
            } catch (Exception kafkaException) {
                log.warn("Failed to send HTTP income request log to Kafka: {}", kafkaException.getMessage());
            }

            
            logToConsole(httpIncomeRequestLogDto);

        } catch (Exception aspectException) {
            log.error("Error in HttpIncomeRequestLogAspect while processing incoming request: {}", 
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

    
    private String extractRequestUri(HttpIncomeRequestLog annotation, JoinPoint joinPoint) {
        
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

    
    private void logToConsole(HttpIncomeRequestLogDto httpIncomeRequestLogDto) {
        String logMessage = String.format(
                "HTTP INCOME REQUEST [INFO] %s %s in %s | Parameters: %s | Body: %s | Description: %s",
                httpIncomeRequestLogDto.httpMethod(),
                httpIncomeRequestLogDto.requestUri(),
                httpIncomeRequestLogDto.methodSignature(),
                httpIncomeRequestLogDto.requestParameters(),
                httpIncomeRequestLogDto.requestBody(),
                httpIncomeRequestLogDto.description()
        );

        log.info(logMessage);
    }
}

package org.example.starter.observability.aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.starter.observability.annotation.HttpIncomeRequestLog;
import org.example.starter.observability.dto.HttpIncomeRequestLogDto;
import org.example.starter.observability.kafka.HttpIncomeRequestLogProducer;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ivakov Andrey
 * Аспект для логирования входящих HTTP-запросов
 * 
 * Обрабатывает методы, аннотированные @HttpIncomeRequestLog:
 * 1. Отправляет сообщение в Kafka топик service_logs
 * 2. Логирует информацию о входящем HTTP запросе с уровнем INFO
 * 3. Включает URI, параметры, тело запроса и описание
 */
@Aspect
public class HttpIncomeRequestLogAspect {

    private static final Logger log = LoggerFactory.getLogger(HttpIncomeRequestLogAspect.class);
    private final HttpIncomeRequestLogProducer httpIncomeRequestLogProducer;

    public HttpIncomeRequestLogAspect(HttpIncomeRequestLogProducer httpIncomeRequestLogProducer) {
        this.httpIncomeRequestLogProducer = httpIncomeRequestLogProducer;
    }

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
            log.error("Error in HttpIncomeRequestLogAspect: {}", aspectException.getMessage(), aspectException);
        }
    }

    private String buildMethodSignature(Method method) {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() +
                Arrays.stream(method.getParameterTypes())
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining(",", "(", ")"));
    }

    private List<String> extractMethodParameters(JoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs())
                .map(arg -> arg == null ? "null" : arg.toString())
                .collect(Collectors.toList());
    }

    private String extractRequestUri(HttpIncomeRequestLog httpIncomeRequestLog, JoinPoint joinPoint) {
        return httpIncomeRequestLog.uri();
    }

    private String extractRequestBody(JoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg != null && !(arg instanceof String) && !(arg instanceof Number))
                .findFirst()
                .map(Object::toString)
                .orElse("");
    }

    private void logToConsole(HttpIncomeRequestLogDto dto) {
        log.info("HTTP INCOME REQUEST [INFO] {} {} in {} : {} | Parameters: {} | Body: {} | Description: {}",
                dto.httpMethod(), dto.requestUri(), dto.serviceName(), dto.methodSignature(), dto.requestParameters(), dto.requestBody(), dto.description());
    }
}



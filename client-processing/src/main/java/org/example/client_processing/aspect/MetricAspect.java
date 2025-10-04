package org.example.client_processing.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.client_processing.annotation.Metric;
import org.example.client_processing.dto.MetricLogDto;
import org.example.client_processing.kafka.ServiceLogProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ivakov Andrey
 * Аспект для замера времени работы метода
 *
 * Обрабатывает методы, аннотированные @Metric:
 * Отправляет сообщение в Kafka топик service_logs, если время работы больше, чем указано
 * в yaml файле
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MetricAspect {

    private final ServiceLogProducer serviceLogProducer;

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${client-processing.metric.execution-time-limit-ms}")
    private Long executionTimeLimitMs;

    @Pointcut("@annotation(org.example.client_processing.annotation.Metric)")
    public void metricAnnotatedMethods() {}

    @Around("metricAnnotatedMethods()")
    public Object executionTimeAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > executionTimeLimitMs) {
                sendMetricWarning(joinPoint, executionTime);
            }
            
            return result;
            
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > executionTimeLimitMs) {
                sendMetricWarning(joinPoint, executionTime);
            }
            
            throw throwable;
        }
    }

    private void sendMetricWarning(ProceedingJoinPoint joinPoint, long executionTime) {
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            String methodSignatureStr = buildMethodSignature(method);
            
            Metric metric = method.getAnnotation(Metric.class);
            String description = (metric != null && !metric.description().isEmpty()) ? 
                metric.description() : "Method execution time exceeded limit";
            
            List<String> methodParameters = extractMethodParameters(joinPoint);

            MetricLogDto metricLogDto = new MetricLogDto(
                    LocalDateTime.now(),
                    serviceName,
                    methodSignatureStr,
                    "WARNING",
                    executionTime,
                    methodParameters,
                    description
            );

            try {
                serviceLogProducer.sendMetricLog(metricLogDto);
            } catch (Exception kafkaException) {
                log.warn("Failed to send metric log to Kafka: {}", kafkaException.getMessage());
            }

            logToConsole(metricLogDto);

        } catch (Exception aspectException) {
            log.error("Error in MetricAspect: {}", aspectException.getMessage(), aspectException);
        }
    }

    private String buildMethodSignature(Method method) {
        StringBuilder signature = new StringBuilder();
        signature.append(method.getDeclaringClass().getSimpleName()).append(".");
        signature.append(method.getName()).append("(");
        
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) signature.append(", ");
            signature.append(paramTypes[i].getSimpleName());
        }
        
        signature.append(") : ").append(method.getReturnType().getSimpleName());
        return signature.toString();
    }

    private List<String> extractMethodParameters(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return List.of();
        
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    String argStr = arg.toString();
                    return argStr.length() > 200 ? argStr.substring(0, 200) + "..." : argStr;
                })
                .collect(Collectors.toList());
    }

    private void logToConsole(MetricLogDto metricLogDto) {
        String logMessage = String.format(
                "METRIC WARNING [%s] %s executed in %dms (limit: %dms) | Parameters: %s | Description: %s",
                metricLogDto.type(),
                metricLogDto.methodSignature(),
                metricLogDto.executionTimeMs(),
                executionTimeLimitMs,
                metricLogDto.methodParameters(),
                metricLogDto.description()
        );
        log.warn(logMessage);
    }
}

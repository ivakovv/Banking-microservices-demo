package org.example.credit_processing.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.credit_processing.annotation.LogDatasourceError;
import org.example.credit_processing.dto.ErrorLogDto;
import org.example.credit_processing.kafka.ServiceLogProducer;
import org.example.credit_processing.model.ErrorLog;
import org.example.credit_processing.repository.ErrorLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ivakov Andrey
 * Аспект для логирования ошибок источника данных
 * 
 * Обрабатывает методы, аннотированные @LogDatasourceError:
 * 1. Отправляет ошибку в Kafka топик service_logs
 * 2. При недоступности Kafka сохраняет в БД таблицу error_log
 * 3. Выводит информацию об ошибке в консольный лог
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogDatasourceErrorAspect {

    private final ServiceLogProducer serviceLogProducer;
    private final ErrorLogRepository errorLogRepository;

    @Value("${spring.application.name}")
    private String serviceName;

    @AfterThrowing(pointcut = "@annotation(logDatasourceError)", throwing = "exception")
    public void logDatasourceError(JoinPoint joinPoint, LogDatasourceError logDatasourceError, Throwable exception) {
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            String methodSignatureStr = buildMethodSignature(method);
            
            List<String> methodParameters = extractMethodParameters(joinPoint);
            
            String stackTrace = getStackTrace(exception);

            ErrorLogDto errorLogDto = new ErrorLogDto(
                    LocalDateTime.now(),
                    serviceName,
                    methodSignatureStr,
                    exception.getMessage(),
                    stackTrace,
                    methodParameters,
                    logDatasourceError.level(),
                    logDatasourceError.description()
            );

            boolean kafkaSent = false;
            try {
                kafkaSent = serviceLogProducer.sendErrorLogSync(errorLogDto);
            } catch (Exception kafkaException) {
                log.warn("Failed to send error log to Kafka, will fallback to database: {}", 
                        kafkaException.getMessage());
            }

            if (!kafkaSent) {
                saveErrorLogToDatabase(errorLogDto);
            }

            logToConsole(errorLogDto, exception);

        } catch (Exception aspectException) {
            log.error("Error in LogDatasourceErrorAspect while processing exception: {}", 
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

    
    private String getStackTrace(Throwable exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }

    
    private void saveErrorLogToDatabase(ErrorLogDto errorLogDto) {
        try {
            ErrorLog errorLog = new ErrorLog();
            errorLog.setTimestamp(errorLogDto.timestamp());
            errorLog.setServiceName(errorLogDto.serviceName());
            errorLog.setMethodSignature(errorLogDto.methodSignature());
            errorLog.setExceptionMessage(errorLogDto.exceptionMessage());
            errorLog.setStackTrace(errorLogDto.stackTrace());
            errorLog.setMethodParameters(String.join(", ", errorLogDto.methodParameters()));
            errorLog.setLogLevel(errorLogDto.logLevel());
            errorLog.setDescription(errorLogDto.description());

            errorLogRepository.save(errorLog);
            log.info("Error log saved to database successfully");
            
        } catch (Exception dbException) {
            log.error("Failed to save error log to database: {}", dbException.getMessage(), dbException);
        }
    }

    
    private void logToConsole(ErrorLogDto errorLogDto, Throwable exception) {
        String logMessage = String.format(
                "DATASOURCE ERROR [%s] in %s: %s | Parameters: %s | Description: %s",
                errorLogDto.logLevel(),
                errorLogDto.methodSignature(),
                errorLogDto.exceptionMessage(),
                errorLogDto.methodParameters(),
                errorLogDto.description()
        );

        switch (errorLogDto.logLevel()) {
            case ERROR -> log.error(logMessage, exception);
            case WARNING -> log.warn(logMessage, exception);
            case INFO -> log.info(logMessage, exception);
        }
    }
}

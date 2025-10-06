package org.example.account_processing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ivakov Andrey
 * Аннотация для логирования ошибок источника данных.
 * При возникновении исключения в аннотированном методе:
 * 1. Отправляет сообщение в Kafka топик service_logs
 * 2. При недоступности Kafka сохраняет в БД таблицу error_log
 * 3. Выводит информацию об ошибке в консольный лог
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogDatasourceError {
    
    LogLevel level() default LogLevel.ERROR;

    String description() default "";
    
    
    enum LogLevel {
        ERROR, WARNING, INFO
    }
}

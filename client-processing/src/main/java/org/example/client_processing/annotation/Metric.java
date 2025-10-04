package org.example.client_processing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ivakov Andrey
 * Аннотация для замера времени работы метода.
 * Обрабатывает методы, аннотированные @Metric:
 * Отправляет сообщение в Kafka топик service_logs, если время работы больше, чем указано
 * в yaml файле
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Metric {
    
    String description() default "";
}

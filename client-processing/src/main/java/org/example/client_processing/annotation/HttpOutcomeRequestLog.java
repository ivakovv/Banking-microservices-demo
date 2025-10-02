package org.example.client_processing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ivakov Andrey
 * Аннотация для логирования успешных HTTP-запросов.
 * При успешном выполнении аннотированного метода:
 * 1. Отправляет сообщение в Kafka топик service_logs
 * 2. Логирует информацию о HTTP запросе с уровнем INFO
 * 3. Включает URI, параметры, тело запроса и ответа
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpOutcomeRequestLog {
    

    String uri() default "";

    String httpMethod() default "GET";

    String description() default "";
}

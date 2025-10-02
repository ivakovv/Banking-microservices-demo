package org.example.account_processing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ivakov Andrey
 * Аннотация для логирования входящих HTTP-запросов.
 * При вызове аннотированного метода (Before):
 * 1. Отправляет сообщение в Kafka топик service_logs
 * 2. Логирует информацию о входящем HTTP запросе с уровнем INFO
 * 3. Включает URI, параметры, тело запроса
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpIncomeRequestLog {
    

    String uri() default "";

    String httpMethod() default "GET";

    String description() default "";
}

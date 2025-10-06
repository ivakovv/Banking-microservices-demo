package org.example.client_processing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ivakov Andrey
 * Аннотация для кэширования результатов методов.
 * 
 * Кэширует записи из БД:
 *      Перед выполнением запроса в БД кэш проверяется на наличие нужной записи
 *      Если записи нет - выполняется запрос и результат сохраняется в кэш
 *      В качестве ключа используется primary key или hashCode объекта
 *      Время хранения записи в кэше задается в application.yml
 *      По истечению времени запись удаляется из кэша
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {
    
    String description() default "";
    
    long ttlSeconds() default -1;
}

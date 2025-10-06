package org.example.client_processing.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.client_processing.annotation.Cached;
import org.example.client_processing.service.CacheService;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author Ivakov Andrey
 * Аспект для кэширования результатов методов, аннотированных @Cached.
 * 
 * Функциональность:
 *      Проверяет кэш перед выполнением метода
 *      Если данные есть в кэше - возвращает их
 *      Если данных нет - выполняет метод и сохраняет результат в кэш
 *      Поддерживает настройку времени жизни кэша через аннотацию и конфигурацию
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CachedAspect {

    private final CacheService cacheService;

    @Pointcut("@annotation(org.example.client_processing.annotation.Cached)")
    public void cachedAnnotatedMethods() {}

    @Around("cachedAnnotatedMethods()")
    public Object cacheAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Cached cachedAnnotation = method.getAnnotation(Cached.class);
        
        String cacheKey = cacheService.createCacheKey(method, joinPoint.getArgs());
        
        log.debug("Processing cached method: {} with key: {}", method.getName(), cacheKey);
        
        try {
            Object cachedResult = cacheService.get(cacheKey);
            if (cachedResult != null) {
                log.debug("Cache hit for method: {} with key: {}", method.getName(), cacheKey);
                return cachedResult;
            }
            
            log.debug("Cache miss for method: {} with key: {}, executing method", method.getName(), cacheKey);
            Object result = joinPoint.proceed();
            
            if (result != null) {
                long ttlSeconds = cachedAnnotation.ttlSeconds();
                cacheService.put(cacheKey, result, ttlSeconds);
                log.debug("Result cached for method: {} with key: {} and TTL: {} seconds", 
                         method.getName(), cacheKey, ttlSeconds > 0 ? ttlSeconds : "default");
            }
            
            return result;
            
        } catch (Throwable throwable) {
            log.error("Error in cached method: {} with key: {}", method.getName(), cacheKey, throwable);
            throw throwable;
        }
    }
}

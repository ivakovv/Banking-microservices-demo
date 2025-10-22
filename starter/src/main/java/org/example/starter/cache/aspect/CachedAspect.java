package org.example.starter.cache.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.starter.cache.CacheService;
import org.example.starter.cache.annotation.Cached;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author Ivakov Andrey
 * Аспект для кэширования результатов выполнения методов
 * 
 * Обрабатывает методы, аннотированные @Cached:
 * 1. Проверяет кэш перед выполнением метода
 * 2. Если результат есть в кэше - возвращает его
 * 3. Если нет - выполняет метод и сохраняет результат в кэш
 * 4. Автоматически удаляет записи по истечении TTL
 */
@Aspect
public class CachedAspect {

    private static final Logger log = LoggerFactory.getLogger(CachedAspect.class);
    private final CacheService cacheService;

    public CachedAspect(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Pointcut("@annotation(org.example.starter.cache.annotation.Cached)")
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



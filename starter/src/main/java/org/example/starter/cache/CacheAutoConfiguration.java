package org.example.starter.cache;

import org.example.starter.cache.aspect.CachedAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(CacheProperties.class)
@ConditionalOnProperty(prefix = "caching", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CacheService cacheService(CacheProperties properties) {
        return new CacheService(properties.getDefaultTtlSeconds(), properties.getCleanupIntervalSeconds());
    }

    @Bean
    @ConditionalOnMissingBean
    public CachedAspect cachedAspect(CacheService cacheService) {
        return new CachedAspect(cacheService);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheLifecycle cacheLifecycle(CacheService cacheService) {
        return new CacheLifecycle(cacheService);
    }
}



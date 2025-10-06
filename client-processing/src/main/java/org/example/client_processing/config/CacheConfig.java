package org.example.client_processing.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client_processing.service.CacheService;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CacheConfig {

    private final CacheService cacheService;

    @PostConstruct
    public void initializeCache() {
        log.info("Initializing cache service");
        cacheService.startCleanupScheduler();
        log.info("Cache service initialized successfully");
    }

    @PreDestroy
    public void shutdownCache() {
        log.info("Shutting down cache service");
        cacheService.stopCleanupScheduler();
        cacheService.clear();
        log.info("Cache service shutdown completed");
    }
}

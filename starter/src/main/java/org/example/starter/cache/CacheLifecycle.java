package org.example.starter.cache;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheLifecycle {

    private static final Logger log = LoggerFactory.getLogger(CacheLifecycle.class);
    private final CacheService cacheService;

    public CacheLifecycle(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostConstruct
    public void start() {
        log.info("Initializing cache service");
        cacheService.startCleanupScheduler();
        log.info("Cache service initialized successfully");
    }

    @PreDestroy
    public void stop() {
        log.info("Shutting down cache service");
        cacheService.stopCleanupScheduler();
        cacheService.clear();
        log.info("Cache service shutdown completed");
    }
}



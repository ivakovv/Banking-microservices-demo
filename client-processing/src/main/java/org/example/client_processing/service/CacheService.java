package org.example.client_processing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CacheService {

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @Value("${client-processing.cache.default-ttl-seconds}")
    private long defaultTtlSeconds;
    
    @Value("${client-processing.cache.cleanup-interval-seconds}")
    private long cleanupIntervalSeconds;

    
    public Object get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            log.debug("Cache hit for key: {}", key);
            return entry.getValue();
        }
        
        if (entry != null && entry.isExpired()) {
            cache.remove(key);
            log.debug("Cache entry expired and removed for key: {}", key);
        }
        
        log.debug("Cache miss for key: {}", key);
        return null;
    }

    
    public void put(String key, Object value, long ttlSeconds) {
        long actualTtl = ttlSeconds > 0 ? ttlSeconds : defaultTtlSeconds;
        CacheEntry entry = new CacheEntry(value, LocalDateTime.now().plusSeconds(actualTtl));
        cache.put(key, entry);
        log.debug("Cache put for key: {} with TTL: {} seconds", key, actualTtl);
    }

    
    public void evict(String key) {
        cache.remove(key);
        log.debug("Cache evicted for key: {}", key);
    }

    
    public void clear() {
        cache.clear();
        log.debug("Cache cleared");
    }

    
    public String createCacheKey(Method method, Object[] args) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(method.getDeclaringClass().getSimpleName())
                  .append(".")
                  .append(method.getName());
        
        if (args != null && args.length > 0) {
            keyBuilder.append(":");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) keyBuilder.append(",");
                
                Object arg = args[i];
                if (arg != null) {
                    if (hasIdField(arg)) {
                        Object id = getEntityId(arg);
                        keyBuilder.append(id);
                    } else {
                        keyBuilder.append(arg.hashCode());
                    }
                } else {
                    keyBuilder.append("null");
                }
            }
        }
        
        return keyBuilder.toString();
    }

    private boolean hasIdField(Object obj) {
        try {
            Method getIdMethod = obj.getClass().getMethod("getId");
            return getIdMethod != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private Object getEntityId(Object obj) {
        try {
            Method getIdMethod = obj.getClass().getMethod("getId");
            return getIdMethod.invoke(obj);
        } catch (Exception e) {
            log.warn("Could not get ID for object: {}", obj.getClass().getSimpleName());
            return obj.hashCode();
        }
    }

    public void startCleanupScheduler() {
        scheduler.scheduleAtFixedRate(
            this::cleanupExpiredEntries,
            cleanupIntervalSeconds,
            cleanupIntervalSeconds,
            TimeUnit.SECONDS
        );
        log.info("Cache cleanup scheduler started with interval: {} seconds", cleanupIntervalSeconds);
    }

    public void stopCleanupScheduler() {
        scheduler.shutdown();
        log.info("Cache cleanup scheduler stopped");
    }

    private void cleanupExpiredEntries() {
        int removedCount = 0;
        for (String key : cache.keySet()) {
            CacheEntry entry = cache.get(key);
            if (entry != null && entry.isExpired()) {
                cache.remove(key);
                removedCount++;
            }
        }
        
        if (removedCount > 0) {
            log.debug("Cleaned up {} expired cache entries", removedCount);
        }
    }

    public CacheStats getStats() {
        int totalEntries = cache.size();
        int expiredEntries = 0;
        
        for (CacheEntry entry : cache.values()) {
            if (entry.isExpired()) {
                expiredEntries++;
            }
        }
        
        return new CacheStats(totalEntries, expiredEntries);
    }

    private static class CacheEntry {
        private final Object value;
        private final LocalDateTime expirationTime;

        public CacheEntry(Object value, LocalDateTime expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }

        public Object getValue() {
            return value;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expirationTime);
        }
    }

    public static class CacheStats {
        private final int totalEntries;
        private final int expiredEntries;

        public CacheStats(int totalEntries, int expiredEntries) {
            this.totalEntries = totalEntries;
            this.expiredEntries = expiredEntries;
        }

        public int getTotalEntries() {
            return totalEntries;
        }

        public int getExpiredEntries() {
            return expiredEntries;
        }

        public int getActiveEntries() {
            return totalEntries - expiredEntries;
        }
    }
}

package org.example.starter.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "caching")
public class CacheProperties {
    private boolean enabled = true;
    private long defaultTtlSeconds = 300;
    private long cleanupIntervalSeconds = 60;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public long getDefaultTtlSeconds() { return defaultTtlSeconds; }
    public void setDefaultTtlSeconds(long defaultTtlSeconds) { this.defaultTtlSeconds = defaultTtlSeconds; }

    public long getCleanupIntervalSeconds() { return cleanupIntervalSeconds; }
    public void setCleanupIntervalSeconds(long cleanupIntervalSeconds) { this.cleanupIntervalSeconds = cleanupIntervalSeconds; }
}



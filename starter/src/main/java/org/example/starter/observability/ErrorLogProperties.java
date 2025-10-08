package org.example.starter.observability;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "observability.error-log")
public class ErrorLogProperties {
    
    private boolean enabled = true;
    private boolean fallbackToDatabase = false;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isFallbackToDatabase() {
        return fallbackToDatabase;
    }
    
    public void setFallbackToDatabase(boolean fallbackToDatabase) {
        this.fallbackToDatabase = fallbackToDatabase;
    }
}

package org.example.starter.observability;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "observability.metric")
public class MetricProperties {
    
    private boolean enabled = true;
    private Long executionTimeLimitMs = 1000L;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Long getExecutionTimeLimitMs() {
        return executionTimeLimitMs;
    }
    
    public void setExecutionTimeLimitMs(Long executionTimeLimitMs) {
        this.executionTimeLimitMs = executionTimeLimitMs;
    }
}

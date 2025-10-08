package org.example.starter.observability;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "observability.http-request-log")
public class HttpRequestLogProperties {
    
    private boolean enabled = true;
    private boolean outcomeEnabled = true;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isOutcomeEnabled() {
        return outcomeEnabled;
    }
    
    public void setOutcomeEnabled(boolean outcomeEnabled) {
        this.outcomeEnabled = outcomeEnabled;
    }
}

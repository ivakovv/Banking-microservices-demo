package org.example.starter.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
@ConditionalOnClass(HttpSecurity.class)
public class SecurityAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ServiceJwtFilter serviceJwtFilter(JwtTokenProvider jwtTokenProvider) {
        return new ServiceJwtFilter(jwtTokenProvider);
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "inter-service", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ServiceRestClient serviceRestClient(JwtTokenProvider jwtTokenProvider, 
                                              @Qualifier("interServiceRestTemplate") RestTemplate interServiceRestTemplate) {
        return new ServiceRestClient(jwtTokenProvider, interServiceRestTemplate);
    }
    
    @Bean("interServiceRestTemplate")
    @ConditionalOnMissingBean(name = "interServiceRestTemplate")
    public RestTemplate interServiceRestTemplate() {
        return new RestTemplate();
    }
}

package org.example.starter.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
public class ServiceRestClient {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate interServiceRestTemplate;
    
    @Value("${spring.application.name}")
    private String serviceName;

    public <T> ResponseEntity<T> get(String url, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());
        log.debug("Service {} calling GET: {}", serviceName, url);
        return interServiceRestTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    }

    public <T> ResponseEntity<T> post(String url, Object request, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(request, createAuthHeaders());
        log.debug("Service {} calling POST: {}", serviceName, url);
        return interServiceRestTemplate.exchange(url, HttpMethod.POST, entity, responseType);
    }

    public <T> ResponseEntity<T> put(String url, Object request, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(request, createAuthHeaders());
        log.debug("Service {} calling PUT: {}", serviceName, url);
        return interServiceRestTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
    }

    public <T> ResponseEntity<T> delete(String url, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());
        log.debug("Service {} calling DELETE: {}", serviceName, url);
        return interServiceRestTemplate.exchange(url, HttpMethod.DELETE, entity, responseType);
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String serviceToken = jwtTokenProvider.generateServiceToken(serviceName, "SERVICE");
        headers.setBearerAuth(serviceToken);
        headers.set("X-Service-Name", serviceName);
        
        return headers;
    }
}

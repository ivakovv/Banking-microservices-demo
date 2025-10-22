package org.example.credit_processing.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.starter.security.ServiceRestClient;
import org.springframework.http.ResponseEntity;


@RequiredArgsConstructor
@Slf4j
public class BaseHttpClient {

    protected final ServiceRestClient serviceRestClient;
    protected final String baseUrl;


    protected <T> T get(String endpoint, Class<T> responseType) {
        String url = baseUrl + endpoint;
        
        log.debug("Making authenticated GET request to: {}", url);
        ResponseEntity<T> response = serviceRestClient.get(url, responseType);

        if (response.getBody() == null) {
            throw new RuntimeException("Response is null for endpoint: " + endpoint);
        }

        log.info("Successfully received response from: {}", endpoint);
        return response.getBody();
    }
}

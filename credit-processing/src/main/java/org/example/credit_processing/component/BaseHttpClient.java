package org.example.credit_processing.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
public class BaseHttpClient {

    protected final RestTemplate restTemplate;
    protected final String baseUrl;

    protected <T> T get(String endpoint, Class<T> responseType) {
        String url = baseUrl + endpoint;

        T response = restTemplate.getForObject(url, responseType);

        if (response == null) {
            throw new RuntimeException("Response is null for endpoint: " + endpoint);
        }

        log.info("Successfully received response from: {}", endpoint);
        return response;
    }
}

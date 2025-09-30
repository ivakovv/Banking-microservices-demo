package org.example.credit_processing.service.info;

import lombok.extern.slf4j.Slf4j;
import org.example.credit_processing.component.BaseHttpClient;
import org.example.credit_processing.dto.ClientInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ClientInfoService extends BaseHttpClient {

    public ClientInfoService(RestTemplate restTemplate,
                             @Value("${credit-processing.client-service.url}") String clientServiceUrl) {
        super(restTemplate, clientServiceUrl);
    }

    public ClientInfoDto getClientInfo(String clientId) {
        log.info("Making GET request to client-processing: {}", baseUrl);
        ClientInfoDto clientInfo = get("/clients/" + clientId, ClientInfoDto.class);

        log.info("Successfully retrieved client info: {} {} {}",
                clientInfo.firstName(), clientInfo.middleName(), clientInfo.lastName());

        return clientInfo;
    }
}

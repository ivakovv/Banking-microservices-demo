package org.example.credit_processing.service.info;

import lombok.extern.slf4j.Slf4j;
import org.example.starter.observability.annotation.HttpOutcomeRequestLog;
import org.example.starter.observability.annotation.LogDatasourceError;
import org.example.credit_processing.component.BaseHttpClient;
import org.example.credit_processing.dto.ClientInfoDto;
import org.example.starter.security.ServiceRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientInfoService extends BaseHttpClient {

    public ClientInfoService(ServiceRestClient serviceRestClient,
                             @Value("${credit-processing.client-service.url}") String clientServiceUrl) {
        super(serviceRestClient, clientServiceUrl);
    }

    @LogDatasourceError(level = LogDatasourceError.LogLevel.ERROR, 
                        description = "Failed to retrieve client information from client-processing service")
    @HttpOutcomeRequestLog(httpMethod = "GET", 
                          description = "Successful request to get client information from client-processing service")
    public ClientInfoDto getClientInfo(String clientId) {
        log.info("Making GET request to client-processing: {}", baseUrl);
        ClientInfoDto clientInfo = get("/clients/" + clientId, ClientInfoDto.class);

        log.info("Successfully retrieved client info: {} {} {}",
                clientInfo.firstName(), clientInfo.middleName(), clientInfo.lastName());

        return clientInfo;
    }
}

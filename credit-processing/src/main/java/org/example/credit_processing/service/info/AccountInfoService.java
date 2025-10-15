package org.example.credit_processing.service.info;

import lombok.extern.slf4j.Slf4j;
import org.example.credit_processing.component.BaseHttpClient;
import org.example.credit_processing.dto.AccountDto;
import org.example.starter.security.ServiceRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountInfoService extends BaseHttpClient {

    public AccountInfoService(ServiceRestClient serviceRestClient,
                              @Value("${credit-processing.account-service.url}") String accountServiceUrl) {
        super(serviceRestClient, accountServiceUrl);
    }

    public AccountDto getAccountByClientAndProductId(String clientId, String productId) {
        log.info("Making GET request to account-processing: {}", baseUrl);
        try {
            AccountDto accountDto = get("/accounts/clients/" + clientId + "/products/" + productId, AccountDto.class);
            log.info("Successfully retrieved account from account-processing: {}", accountDto);
            return accountDto;
        } catch (Exception e) {
            log.error("Failed to retrieve account for clientId: {}, productId: {}. Error: {}", 
                     clientId, productId, e.getMessage());
            throw new RuntimeException("Account not found for client " + clientId + " with product " + productId, e);
        }
    }
}

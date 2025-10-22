package org.example.account_processing.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.starter.observability.annotation.HttpIncomeRequestLog;
import org.example.account_processing.dto.account.AccountDto;
import org.example.account_processing.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Номер счета успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - требуется межсервисная аутентификация"),
            @ApiResponse(responseCode = "404", description = "Счет не найден"),
            @ApiResponse(responseCode = "503", description = "Сервис не отвечает")
    })
    @GetMapping("/clients/{clientId}/products/{productId}")
    @PreAuthorize("hasRole('SERVICE')")
    @HttpIncomeRequestLog(
        httpMethod = "GET", 
        uri = "/accounts/clients/{clientId}/products/{productId}",
        description = "Incoming request to get account by client and product"
    )
    public ResponseEntity<AccountDto> getAccountByProductId(
            @PathVariable("clientId") String clientId,
            @PathVariable("productId") String productId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountService.getAccountByClientAndProductId(clientId, productId));
    }
}

package org.example.account_processing.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.account_processing.dto.account.AccountDto;
import org.example.account_processing.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @ApiResponse(responseCode = "403", description = "Произошла ошибка при получении счета продукта"),
            @ApiResponse(responseCode = "503", description = "Сервис не отвечает")
    })
    @GetMapping("/clients/{clientId}/products/{productId}")
    public ResponseEntity<AccountDto> getAccountByProductId(
            @PathVariable("clientId") String clientId,
            @PathVariable("productId") String productId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountService.getAccountByClientAndProductId(clientId, productId));
    }
}

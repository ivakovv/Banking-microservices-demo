package org.example.client_processing.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.client_processing.dto.client.ClientDto;
import org.example.client_processing.dto.client.RegistrationRequest;
import org.example.client_processing.dto.client.RegistrationResponse;
import org.example.client_processing.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент успешно создан"),
            @ApiResponse(responseCode = "403", description = "Произошла ошибка при регистрации клиента"),
            @ApiResponse(responseCode = "409", description = "Клиент уже существует"),
            @ApiResponse(responseCode = "503", description = "Сервис не отвечает")
    })
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerClient(@Valid @RequestBody RegistrationRequest registrationRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.register(registrationRequest));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент успешно создан"),
            @ApiResponse(responseCode = "403", description = "Произошла ошибка при регистрации клиента"),
            @ApiResponse(responseCode = "409", description = "Клиент уже существует"),
            @ApiResponse(responseCode = "503", description = "Сервис не отвечает")
    })
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientDto> getClient(@PathVariable("clientId") String clientId){
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.getClientById(clientId));
    }
}

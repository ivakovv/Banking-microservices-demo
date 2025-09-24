package org.example.client_processing.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.client_processing.dto.RegistrationRequest;
import org.example.client_processing.dto.RegistrationResponse;
import org.example.client_processing.service.impl.ClientServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class ClientController {
    private final ClientServiceImpl clientService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент успешно создан"),
            @ApiResponse(responseCode = "403", description = "Произошла ошибка при регистрации клиента"),
            @ApiResponse(responseCode = "503", description = "Сервис не отвечает")
    })
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerClient(@Valid @RequestBody RegistrationRequest registrationRequest){
        return ResponseEntity.ok(clientService.register(registrationRequest));
    }

}

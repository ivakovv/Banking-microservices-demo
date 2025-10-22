package org.example.client_processing.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.client_processing.service.ClientBlockingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/clients")
@RequiredArgsConstructor
public class ClientBlockingController {

    private final ClientBlockingService clientBlockingService;

    @PostMapping("/{clientId}/block")
    @PreAuthorize("hasRole('MASTER') or hasRole('GRAND_EMPLOYEE')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент успешно заблокирован"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для блокировки клиента")
    })
    public ResponseEntity<String> blockClient(
            @PathVariable String clientId,
            @RequestParam(required = false, defaultValue = "Administrative block") String reason) {
        
        clientBlockingService.blockClient(clientId, reason);
        return ResponseEntity.ok("Client " + clientId + " has been blocked. Reason: " + reason);
    }

    @PostMapping("/{clientId}/unblock")
    @PreAuthorize("hasRole('MASTER') or hasRole('GRAND_EMPLOYEE')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент успешно разблокирован"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для разблокировки клиента")
    })
    public ResponseEntity<String> unblockClient(@PathVariable String clientId) {
        clientBlockingService.unblockClient(clientId);
        return ResponseEntity.ok("Client " + clientId + " has been unblocked");
    }

    @GetMapping("/{clientId}/status")
    @PreAuthorize("hasRole('MASTER') or hasRole('GRAND_EMPLOYEE')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус клиента получен"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для просмотра статуса клиента")
    })
    public ResponseEntity<String> getClientStatus(@PathVariable String clientId) {
        boolean isBlocked = clientBlockingService.isClientBlocked(clientId);
        String status = isBlocked ? "BLOCKED" : "ACTIVE";
        return ResponseEntity.ok("Client " + clientId + " status: " + status);
    }
}

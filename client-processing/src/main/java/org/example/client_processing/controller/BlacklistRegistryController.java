package org.example.client_processing.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.client_processing.dto.BlacklistRegistryRequest;
import org.example.client_processing.dto.BlacklistRegistryResponse;
import org.example.client_processing.enums.client.DocumentType;
import org.example.client_processing.service.BlacklistRegistryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/blacklist")
@RequiredArgsConstructor
public class BlacklistRegistryController {

    private final BlacklistRegistryService blacklistRegistryService;

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Документ успешно добавлен в черный список"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "409", description = "Документ, уже занесенный в черный список")
    })
    public ResponseEntity<BlacklistRegistryResponse> addToBlacklist(@Valid @RequestBody BlacklistRegistryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blacklistRegistryService.addToBlackList(request));
    }

    @DeleteMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Документ успешно удален из черного списка"),
            @ApiResponse(responseCode = "404", description = "Документ, не найденный в черном списке")
    })
    public ResponseEntity<Void> removeFromBlacklist(@RequestParam("documentId") String documentId) {
        blacklistRegistryService.deleteFromBlackList(documentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/expiration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Дата истечения срока действия успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Документ, не найденный в черном списке")
    })
    public ResponseEntity<BlacklistRegistryResponse> changeExpirationDate(
            @RequestParam("documentId") String documentId,
            @RequestParam("expirationDate") String expirationDate) {
        
        java.time.LocalDateTime expiration = java.time.LocalDateTime.parse(expirationDate);
        BlacklistRegistryResponse response = blacklistRegistryService.changeExpirationDate(documentId, expiration);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись из черного списка успешно восстановлена"),
            @ApiResponse(responseCode = "404", description = "Документ, не найденный в черном списке")
    })
    public ResponseEntity<BlacklistRegistryResponse> getBlacklistEntry(@RequestParam("documentId") String documentId) {
        return ResponseEntity.ok(blacklistRegistryService.getBlacklistRegistry(documentId));
    }

    @GetMapping("/check")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получен статус черного списка")
    })
    public ResponseEntity<Boolean> isBlacklisted(
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam("documentId") String documentId) {
        return ResponseEntity.ok(blacklistRegistryService.isBlacklisted(documentType, documentId));
    }
}

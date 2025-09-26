package org.example.client_processing.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.client_processing.dto.client_product.ClientProductRequest;
import org.example.client_processing.dto.client_product.ClientProductResponse;
import org.example.client_processing.service.ClientProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/client-products")
@RequiredArgsConstructor
public class ClientProductController {

    private final ClientProductService clientProductService;

    @PostMapping("/client/{clientId}/product/{productId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Продукт клиента успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных или нарушение бизнес-правил"),
            @ApiResponse(responseCode = "404", description = "Клиент или продукт не найден"),
            @ApiResponse(responseCode = "409", description = "Продукт уже выдан клиенту или другому клиенту")
    })
    public ResponseEntity<ClientProductResponse> createClientProduct(
            @PathVariable("clientId") String clientId,
            @PathVariable("productId") String productId,
            @Valid @RequestBody ClientProductRequest request) {
        ClientProductResponse response = clientProductService.create(clientId, productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/client/{clientId}/product/{productId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт клиента найден"),
            @ApiResponse(responseCode = "404", description = "Продукт клиента не найден")
    })
    public ResponseEntity<ClientProductResponse> getClientProductByClientIdAndProductId(
            @PathVariable("clientId") String clientId,
            @PathVariable("productId") String productId) {
        return ResponseEntity.ok(clientProductService.getByClientIdAndProductId(clientId, productId));
    }

    @GetMapping("/client/{clientId}")
    @ApiResponse(responseCode = "200", description = "Список продуктов клиента получен")
    public ResponseEntity<List<ClientProductResponse>> getClientProductsByClientId(@PathVariable("clientId") String clientId) {
        return ResponseEntity.ok(clientProductService.getByClientId(clientId));
    }

    @GetMapping("/client/{clientId}/type/{productType}")
    @ApiResponse(responseCode = "200", description = "Список продуктов клиента по типу получен")
    public ResponseEntity<List<ClientProductResponse>> getClientProductsByClientIdAndType(
            @PathVariable("clientId") String clientId,
            @PathVariable("productType") String productType) {
        return ResponseEntity.ok(clientProductService.getByClientIdAndProductType(clientId, productType));
    }

    @PutMapping("/client/{clientId}/product/{productId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт клиента успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Продукт клиента не найден")
    })
    public ResponseEntity<ClientProductResponse> updateClientProduct(
            @PathVariable("clientId") String clientId,
            @PathVariable("productId") String productId, 
            @Valid @RequestBody ClientProductRequest request) {
        return ResponseEntity.ok(clientProductService.updateByClientIdAndProductId(clientId, productId, request));
    }

    @DeleteMapping("/client/{clientId}/product/{productId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Продукт клиента успешно удален"),
            @ApiResponse(responseCode = "404", description = "Продукт клиента не найден")
    })
    public ResponseEntity<Void> deleteClientProduct(
            @PathVariable("clientId") String clientId,
            @PathVariable("productId") String productId) {
        clientProductService.deleteByClientIdAndProductId(clientId, productId);
        return ResponseEntity.noContent().build();
    }
}

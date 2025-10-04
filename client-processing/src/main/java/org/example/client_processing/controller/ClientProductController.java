package org.example.client_processing.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.client_processing.annotation.Metric;
import org.example.client_processing.dto.client_product.ClientProductRequest;
import org.example.client_processing.dto.client_product.ClientProductResponse;
import org.example.client_processing.dto.client_product.ReleaseCardRequest;
import org.example.client_processing.dto.client_product.ReleaseCardResponse;
import org.example.client_processing.service.ClientProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/client-products")
@RequiredArgsConstructor
public class ClientProductController {

    private final ClientProductService clientProductService;

    @PostMapping("/client/{clientId}/product/{productId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Продукт клиента успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Клиент или продукт не найден")
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

    @Metric(description = "Release card for client - testing metric aspect")
    @PostMapping("/client/{clientId}/product/cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заявка на открытие карты успешно получена"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    public ResponseEntity<ReleaseCardResponse> createClientProduct(
            @PathVariable("clientId") String clientId,
            @Valid @RequestBody ReleaseCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientProductService.releaseCard(clientId, request));
    }
}

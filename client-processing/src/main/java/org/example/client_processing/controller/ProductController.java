package org.example.client_processing.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.client_processing.dto.product.ProductRequest;
import org.example.client_processing.dto.product.ProductResponse;
import org.example.client_processing.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('MASTER')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Продукт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания продукта"),
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт найден"),
            @ApiResponse(responseCode = "404", description = "Продукт не найден")
    })
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("productId") String productId) {
        return ResponseEntity.ok(productService.getByProductId(productId));
    }

    @GetMapping("/all")
    @ApiResponse(responseCode = "200", description = "Список продуктов получен")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('MASTER') or hasRole('GRAND_EMPLOYEE')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Продукт не найден"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для редактирования продукта")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("productId") String productId,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.update(productId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('MASTER') or hasRole('GRAND_EMPLOYEE')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Продукт успешно удален"),
            @ApiResponse(responseCode = "404", description = "Продукт не найден"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для удаления продукта")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") String productId) {
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
package org.example.client_processing.dto.client_product;

import org.example.client_processing.enums.client_product.Status;
import java.time.LocalDateTime;

public record ClientProductEventDto(
        Long clientProductId,
        String clientId,
        String productId,
        String productName,
        String productType,
        String eventType,
        LocalDateTime openDate,
        LocalDateTime closeDate,
        Status status,
        LocalDateTime timestamp,
        String description
) {

    public static ClientProductEventDto createdClientProduct(Long clientProductId, String clientId, String productId,
                                                             String productName, String productType, LocalDateTime openDate,
                                                             LocalDateTime closeDate, Status status) {
        return new ClientProductEventDto(
                clientProductId,
                clientId,
                productId,
                productName,
                productType,
                "CLIENT_PRODUCT_CREATED",
                openDate,
                closeDate,
                status,
                LocalDateTime.now(),
                "Client product created successfully"
        );
    }

    public static ClientProductEventDto updatedClientProduct(Long clientProductId, String clientId, String productId,
                                                             String productName, String productType, LocalDateTime openDate,
                                                             LocalDateTime closeDate, Status status) {
        return new ClientProductEventDto(
                clientProductId,
                clientId,
                productId,
                productName,
                productType,
                "CLIENT_PRODUCT_UPDATED",
                openDate,
                closeDate,
                status,
                LocalDateTime.now(),
                "Client product updated successfully"
        );
    }

    public static ClientProductEventDto deletedClientProduct(Long clientProductId, String clientId, String productId,
                                                             String productName, String productType, LocalDateTime openDate,
                                                             LocalDateTime closeDate, Status status) {
        return new ClientProductEventDto(
                clientProductId,
                clientId,
                productId,
                productName,
                productType,
                "CLIENT_PRODUCT_DELETED",
                openDate,
                closeDate,
                status,
                LocalDateTime.now(),
                "Client product deleted successfully"
        );
    }
}
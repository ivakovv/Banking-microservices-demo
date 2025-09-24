package org.example.client_processing.dto;

import org.example.client_processing.enums.product.Key;
import java.time.LocalDateTime;

public record ProductResponse(
        String name,
        Key key,
        LocalDateTime createDate,
        String productId
) {}

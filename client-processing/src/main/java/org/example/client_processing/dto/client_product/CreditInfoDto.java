package org.example.client_processing.dto.client_product;

import java.math.BigDecimal;

public record CreditInfoDto(
        BigDecimal creditAmount,
        BigDecimal interestRate
) {
}

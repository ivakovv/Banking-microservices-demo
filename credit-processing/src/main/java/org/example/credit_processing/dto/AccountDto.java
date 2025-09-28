package org.example.credit_processing.dto;

import java.math.BigDecimal;

public record AccountDto(
        Long id,
        String clientId,
        String productId,
        BigDecimal balance,
        BigDecimal interestRate,
        Boolean isRecalc,
        Boolean cardExist,
        String status
) {
}

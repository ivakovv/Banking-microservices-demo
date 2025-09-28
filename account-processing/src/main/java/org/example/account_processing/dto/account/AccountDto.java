package org.example.account_processing.dto.account;

import org.example.account_processing.enums.Status;

import java.math.BigDecimal;

public record AccountDto(
        Long id,
        String clientId,
        String productId,
        BigDecimal balance,
        BigDecimal interestRate,
        Boolean isRecalc,
        Boolean cardExist,
        Status status
) {
}

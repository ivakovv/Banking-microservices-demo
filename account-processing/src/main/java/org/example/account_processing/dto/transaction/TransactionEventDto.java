package org.example.account_processing.dto.transaction;

import org.example.account_processing.enums.Type;

import java.math.BigDecimal;

public record TransactionEventDto(
        Long accountId,
        String cardId,
        Type type,
        BigDecimal amount) {
}

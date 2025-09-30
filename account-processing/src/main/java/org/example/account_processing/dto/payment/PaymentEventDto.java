package org.example.account_processing.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentEventDto(
        Long accountId,
        LocalDateTime paymentDate,
        BigDecimal amount,
        LocalDateTime payedAt) {
}

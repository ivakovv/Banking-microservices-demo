package org.example.credit_processing.dto;

import java.math.BigDecimal;

public record CreditDecisionDto(
        String clientId,
        String productId,
        BigDecimal requestedAmount,
        BigDecimal totalExistingDebt,
        BigDecimal creditLimit,
        Boolean hasOverduePayments,
        Boolean approved,
        String reason,
        BigDecimal monthlyPayment,
        BigDecimal totalPayment,
        BigDecimal overpayment
) {
}

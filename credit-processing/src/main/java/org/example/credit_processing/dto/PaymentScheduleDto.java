package org.example.credit_processing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PaymentScheduleDto(
        Long productRegistryId,
        BigDecimal totalAmount,
        BigDecimal monthlyPayment,
        BigDecimal totalPayment,
        BigDecimal overpayment,
        List<PaymentEntryDto> payments
) {
    
    public record PaymentEntryDto(
            Integer paymentNumber,
            LocalDate paymentDate,
            BigDecimal totalAmount,
            BigDecimal interestAmount,
            BigDecimal principalAmount,
            BigDecimal remainingBalance
    ) {}
}

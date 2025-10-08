package org.example.credit_processing.util;

import lombok.extern.slf4j.Slf4j;
import org.example.credit_processing.dto.PaymentScheduleDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivakov Andrey
 * Утилита для расчета аннуитетных платежей
 */
@Component
@Slf4j
public class AnnuityCalculator {

    /**
     * Рассчитывает аннуитетный платеж по формуле: A = S × [i × (1 + i)^n] / [(1 + i)^n - 1]
     * где:
     * A — размер аннуитетного взноса
     * S — сумма кредита
     * i — месячная процентная ставка (годовая ставка / 12)
     * n — количество периодов (месяцев)
     */
    public BigDecimal calculateAnnuityPayment(BigDecimal principal, BigDecimal annualRate, int months) {
        if (principal == null || annualRate == null || months <= 0) {
            throw new IllegalArgumentException("Invalid parameters for annuity calculation");
        }

        // Месячная процентная ставка
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        // (1 + i)^n
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal powerTerm = onePlusRate.pow(months);

        // i × (1 + i)^n
        BigDecimal numerator = monthlyRate.multiply(powerTerm);

        // (1 + i)^n - 1
        BigDecimal denominator = powerTerm.subtract(BigDecimal.ONE);

        // A = S × [i × (1 + i)^n] / [(1 + i)^n - 1]
        BigDecimal annuityPayment = principal.multiply(numerator).divide(denominator, 2, RoundingMode.HALF_UP);

        log.info("Calculated annuity payment: {} for principal: {}, rate: {}%, months: {}", 
                annuityPayment, principal, annualRate, months);

        return annuityPayment;
    }

    /**
     * Создает график платежей с разбивкой на проценты и основной долг
     */
    public PaymentScheduleDto createPaymentSchedule(BigDecimal principal, BigDecimal annualRate, 
                                                   int months, LocalDate startDate) {
        
        BigDecimal monthlyPayment = calculateAnnuityPayment(principal, annualRate, months);
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        
        List<PaymentScheduleDto.PaymentEntryDto> payments = new ArrayList<>();
        BigDecimal remainingBalance = principal;
        
        for (int i = 1; i <= months; i++) {
            // Проценты за текущий месяц: Проценты = Остаток долга × r
            BigDecimal interestAmount = remainingBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            
            // Основной долг: Основной долг = P − Проценты
            BigDecimal principalAmount = monthlyPayment.subtract(interestAmount);
            
            // Если это последний платеж, корректируем сумму
            if (i == months) {
                principalAmount = remainingBalance;
                monthlyPayment = principalAmount.add(interestAmount);
            }
            
            // Новый остаток = Старый остаток − Основной долг
            remainingBalance = remainingBalance.subtract(principalAmount);
            
            LocalDate paymentDate = startDate.plusMonths(i);
            
            payments.add(new PaymentScheduleDto.PaymentEntryDto(
                    i,
                    paymentDate,
                    monthlyPayment,
                    interestAmount,
                    principalAmount,
                    remainingBalance.max(BigDecimal.ZERO)
            ));
        }
        
        BigDecimal totalPayment = monthlyPayment.multiply(BigDecimal.valueOf(months));
        BigDecimal overpayment = totalPayment.subtract(principal);
        
        log.info("Created payment schedule: {} payments, total: {}, overpayment: {}", 
                months, totalPayment, overpayment);
        
        return new PaymentScheduleDto(
                null,
                principal,
                monthlyPayment,
                totalPayment,
                overpayment,
                payments
        );
    }
}

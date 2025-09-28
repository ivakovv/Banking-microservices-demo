package org.example.credit_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.credit_processing.dto.PaymentScheduleDto;
import org.example.credit_processing.model.PaymentRegistry;
import org.example.credit_processing.model.ProductRegistry;
import org.example.credit_processing.service.PaymentScheduleService;
import org.example.credit_processing.util.AnnuityCalculator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduleServiceImpl implements PaymentScheduleService {

    private final AnnuityCalculator annuityCalculator;

    @Override
    public PaymentScheduleDto createPaymentSchedule(ProductRegistry productRegistry) {
        log.info("Creating payment schedule for product registry: {}", productRegistry.getId());

        // Создаем график платежей
        PaymentScheduleDto schedule = annuityCalculator.createPaymentSchedule(
                productRegistry.getAmount(),
                productRegistry.getInterestRate(),
                productRegistry.getMonthCount(),
                productRegistry.getOpenDate().toLocalDate()
        );

        log.info("Created payment schedule with {} payments, total amount: {}, monthly payment: {}", 
                schedule.payments().size(), schedule.totalPayment(), schedule.monthlyPayment());

        return schedule;
    }

    @Override
    public List<PaymentRegistry> createPaymentRegistries(ProductRegistry productRegistry, 
                                                         PaymentScheduleDto schedule) {
        log.info("Creating payment registries for product registry: {}", productRegistry.getId());

        return schedule.payments().stream()
                .map(payment -> {
                    PaymentRegistry registry = new PaymentRegistry();
                    registry.setProductRegistry(productRegistry);
                    registry.setPaymentDate(payment.paymentDate());
                    registry.setAmount(payment.totalAmount());
                    registry.setInterestRateAmount(payment.interestAmount());
                    registry.setDebtAmount(payment.principalAmount());
                    registry.setExpired(false);
                    registry.setPaymentExpirationDate(payment.paymentDate().atStartOfDay().plusDays(30));
                    
                    return registry;
                })
                .toList();
    }
}

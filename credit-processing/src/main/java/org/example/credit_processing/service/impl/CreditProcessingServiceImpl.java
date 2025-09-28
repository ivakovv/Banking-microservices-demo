package org.example.credit_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.credit_processing.dto.AccountDto;
import org.example.credit_processing.dto.CreditDecisionDto;
import org.example.credit_processing.dto.PaymentScheduleDto;
import org.example.credit_processing.mapper.CreditDecisionMapper;
import org.example.credit_processing.model.ProductRegistry;
import org.example.credit_processing.service.CreditDecisionService;
import org.example.credit_processing.service.CreditProcessingService;
import org.example.credit_processing.service.PaymentScheduleService;
import org.example.credit_processing.service.ProductRegistryService;
import org.example.credit_processing.service.info.AccountInfoService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditProcessingServiceImpl implements CreditProcessingService {

    private final CreditDecisionService creditDecisionService;
    private final PaymentScheduleService paymentScheduleService;
    private final ProductRegistryService productRegistryService;
    private final AccountInfoService accountInfoService;
    private final CreditDecisionMapper creditDecisionMapper;

    @Override
    public CreditDecisionDto processCreditProduct(ProductRegistry productRegistry) {
        log.info("Processing credit product for client: {}, product: {}", 
                productRegistry.getClientId(), productRegistry.getProductId());

        // Принятие решения по кредиту
        CreditDecisionDto decision = creditDecisionService.makeCreditDecision(productRegistry);
        
        if (decision.approved()) {
            log.info("Credit approved for client: {}, creating product registry", productRegistry.getClientId());

            // Получение accountId от account-processing
            AccountDto accountDto = accountInfoService.getAccountByClientAndProductId(
                    productRegistry.getClientId(), productRegistry.getProductId());
            productRegistry.setAccountId(accountDto.id());
            log.info("Set account ID {} for product registry", accountDto.id());
            
            // Создание графика платежей
            PaymentScheduleDto schedule = paymentScheduleService.createPaymentSchedule(productRegistry);
            
            // Сохранение продукта и графика платежей
            ProductRegistry savedProduct = productRegistryService.saveProductWithPaymentSchedule(productRegistry, schedule);
            log.info("Saved product registry with ID: {}", savedProduct.getId());

            return creditDecisionMapper.createApprovalDecisionWithPayments(
                    productRegistry,
                    decision.totalExistingDebt(),
                    decision.creditLimit(),
                    decision.hasOverduePayments(),
                    schedule.monthlyPayment(),
                    schedule.totalPayment(),
                    schedule.overpayment()
            );
        } else {
            log.info("Credit rejected for client: {}, reason: {}", 
                    productRegistry.getClientId(), decision.reason());
            return decision;
        }
    }

    @Override
    public PaymentScheduleDto createPaymentSchedule(ProductRegistry productRegistry) {
        log.info("Creating payment schedule for approved credit: client={}, product={}", 
                productRegistry.getClientId(), productRegistry.getProductId());

        AccountDto accountDto = accountInfoService.getAccountByClientAndProductId(
                productRegistry.getClientId(), productRegistry.getProductId());
        productRegistry.setAccountId(accountDto.id());
        
        return paymentScheduleService.createPaymentSchedule(productRegistry);
    }
}

package org.example.credit_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.credit_processing.dto.ClientInfoDto;
import org.example.credit_processing.dto.CreditDecisionDto;
import org.example.credit_processing.mapper.CreditDecisionMapper;
import org.example.credit_processing.model.ProductRegistry;
import org.example.credit_processing.repository.ProductRegistryRepository;
import org.example.credit_processing.service.CreditDecisionService;
import org.example.credit_processing.service.info.ClientInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditDecisionServiceImpl implements CreditDecisionService {

    private final ClientInfoService clientInfoService;
    private final ProductRegistryRepository productRegistryRepository;
    private final CreditDecisionMapper creditDecisionMapper;
    
    @Value("${credit-processing.credit-limit}")
    private BigDecimal creditLimit;
    
    @Value("${credit-processing.allow-overdue}")
    private Boolean allowOverdue;

    @Override
    public CreditDecisionDto makeCreditDecision(ProductRegistry productRegistry) {
        log.info("Making credit decision for client: {}, product: {}, amount: {}", 
                productRegistry.getClientId(), productRegistry.getProductId(), productRegistry.getAmount());

        //  обращение к client-processing за dto клиента
        ClientInfoDto clientInfo = clientInfoService.getClientInfo(productRegistry.getClientId());
        log.info("Retrieved client info: {} {} {}", 
                clientInfo.firstName(), clientInfo.middleName(), clientInfo.lastName());

        // Получение информации о существующих кредитных продуктах
        BigDecimal totalExistingDebt = getTotalExistingDebt(productRegistry.getClientId());
        log.info("Total existing debt for client {}: {}", productRegistry.getClientId(), totalExistingDebt);

        // Проверка на просрочки
        boolean hasOverduePayments = checkForOverduePayments(productRegistry.getClientId());
        log.info("Client {} has overdue payments: {}", productRegistry.getClientId(), hasOverduePayments);

        // Принятие решения
        CreditDecisionDto decision = evaluateCreditApplication(productRegistry, totalExistingDebt, hasOverduePayments);
        
        log.info("Credit decision for client {}: approved={}, reason={}", 
                productRegistry.getClientId(), decision.approved(), decision.reason());
        
        return decision;
    }

    private BigDecimal getTotalExistingDebt(String clientId) {
        BigDecimal totalDebt = productRegistryRepository.calculateTotalDebtByClientId(clientId);
        log.info("Total existing debt for client {}: {}", clientId, totalDebt);
        return totalDebt;
    }

    private boolean checkForOverduePayments(String clientId) {
        boolean hasOverdue = productRegistryRepository.hasOverduePayments(clientId);
        log.info("Client {} has overdue payments: {}", clientId, hasOverdue);
        return hasOverdue;
    }

    private CreditDecisionDto evaluateCreditApplication(ProductRegistry productRegistry, 
                                                       BigDecimal totalExistingDebt, 
                                                       boolean hasOverduePayments) {
        
        BigDecimal requestedAmount = productRegistry.getAmount();
        BigDecimal totalDebt = totalExistingDebt.add(requestedAmount);

        if (totalDebt.compareTo(creditLimit) > 0) {
            return creditDecisionMapper.createLimitExceededDecision(
                    productRegistry, totalExistingDebt, creditLimit, hasOverduePayments);
        }

        if (hasOverduePayments && !allowOverdue) {
            return creditDecisionMapper.createOverdueRejectionDecision(
                    productRegistry, totalExistingDebt, creditLimit, hasOverduePayments);
        }

        return creditDecisionMapper.createApprovalDecision(
                productRegistry, totalExistingDebt, creditLimit, hasOverduePayments);
    }
}

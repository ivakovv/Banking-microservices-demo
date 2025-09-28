package org.example.credit_processing.mapper;

import org.example.credit_processing.dto.CreditDecisionDto;
import org.example.credit_processing.model.ProductRegistry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;


@Mapper(componentModel = "spring")
public interface CreditDecisionMapper {

    default CreditDecisionDto createLimitExceededDecision(ProductRegistry productRegistry, 
                                                          BigDecimal totalExistingDebt, 
                                                          BigDecimal creditLimit, 
                                                          Boolean hasOverduePayments) {
        return new CreditDecisionDto(
                productRegistry.getClientId(),
                productRegistry.getProductId(),
                productRegistry.getAmount(),
                totalExistingDebt,
                creditLimit,
                hasOverduePayments,
                false,
                String.format("Превышен кредитный лимит. Запрашиваемая сумма: %s, существующая задолженность: %s, лимит: %s", 
                        productRegistry.getAmount(), totalExistingDebt, creditLimit),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    default CreditDecisionDto createOverdueRejectionDecision(ProductRegistry productRegistry, 
                                                           BigDecimal totalExistingDebt, 
                                                           BigDecimal creditLimit, 
                                                           Boolean hasOverduePayments) {
        return new CreditDecisionDto(
                productRegistry.getClientId(),
                productRegistry.getProductId(),
                productRegistry.getAmount(),
                totalExistingDebt,
                creditLimit,
                hasOverduePayments,
                false,
                "Отказ из-за наличия просрочек по существующим кредитам",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    default CreditDecisionDto createApprovalDecision(ProductRegistry productRegistry, 
                                                    BigDecimal totalExistingDebt, 
                                                    BigDecimal creditLimit, 
                                                    Boolean hasOverduePayments) {
        return new CreditDecisionDto(
                productRegistry.getClientId(),
                productRegistry.getProductId(),
                productRegistry.getAmount(),
                totalExistingDebt,
                creditLimit,
                hasOverduePayments,
                true,
                "Кредит одобрен",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    default CreditDecisionDto createApprovalDecisionWithPayments(ProductRegistry productRegistry, 
                                                               BigDecimal totalExistingDebt, 
                                                               BigDecimal creditLimit, 
                                                               Boolean hasOverduePayments,
                                                               BigDecimal monthlyPayment,
                                                               BigDecimal totalPayment,
                                                               BigDecimal overpayment) {
        return new CreditDecisionDto(
                productRegistry.getClientId(),
                productRegistry.getProductId(),
                productRegistry.getAmount(),
                totalExistingDebt,
                creditLimit,
                hasOverduePayments,
                true,
                "Кредит одобрен и график платежей создан",
                monthlyPayment,
                totalPayment,
                overpayment
        );
    }
}

package org.example.credit_processing.service;

import org.example.credit_processing.dto.CreditDecisionDto;
import org.example.credit_processing.dto.PaymentScheduleDto;
import org.example.credit_processing.model.ProductRegistry;

/**
 * @author Ivakov Andrey
 * Сервис для обработки кредитных продуктов
 */
public interface CreditProcessingService {

    /**
     * Обрабатывает кредитный продукт
     */
    CreditDecisionDto processCreditProduct(ProductRegistry productRegistry);
    
    /**
     * Создает график платежей для одобренного кредита
     */
    PaymentScheduleDto createPaymentSchedule(ProductRegistry productRegistry);
}
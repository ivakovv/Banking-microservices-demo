package org.example.credit_processing.service;

import org.example.credit_processing.dto.PaymentScheduleDto;
import org.example.credit_processing.model.ProductRegistry;

/**
 * @author Ivakov Andrey
 * Сервис для работы с реестром продуктов
 */
public interface ProductRegistryService {
    
    /**
     * Сохраняет продукт и создает график платежей
     */
    ProductRegistry saveProductWithPaymentSchedule(ProductRegistry productRegistry, PaymentScheduleDto schedule);
}

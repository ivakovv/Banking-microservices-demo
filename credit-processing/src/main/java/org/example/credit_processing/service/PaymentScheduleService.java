package org.example.credit_processing.service;

import org.example.credit_processing.dto.PaymentScheduleDto;
import org.example.credit_processing.model.PaymentRegistry;
import org.example.credit_processing.model.ProductRegistry;

import java.util.List;

/**
 * @author Ivakov Andrey
 * Сервис для создания графика платежей
 */
public interface PaymentScheduleService {
    
    /**
     * Создает график платежей для кредитного продукта
     */
    PaymentScheduleDto createPaymentSchedule(ProductRegistry productRegistry);
    
    /**
     * Создает записи PaymentRegistry на основе графика платежей
     */
    List<PaymentRegistry> createPaymentRegistries(ProductRegistry productRegistry, PaymentScheduleDto schedule);
}

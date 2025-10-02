package org.example.credit_processing.service;

import org.example.credit_processing.dto.CreditDecisionDto;
import org.example.credit_processing.model.ProductRegistry;

/**
 * @author Ivakov Andrey
 * Сервис для принятия решений по кредитным продуктам
 */
public interface CreditDecisionService {
    
    /**
     * Принимает решение по кредитному продукту
     */
    CreditDecisionDto makeCreditDecision(ProductRegistry productRegistry);
}

package org.example.account_processing.service;

import org.example.account_processing.model.Payment;

/**
 * @author Ivakov Andrey
 * Сервис для работы c картами
 */
public interface PaymentService {

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод обрабатывает платеж по кредитному счету </b> </i>
     * </p>
     * @param payment готовый объект платежа
     * @return созданный платеж
     */
    Payment processCreditPayment(Payment payment);


    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод обновляет существующие неоплаченные платежи по счету </b> </i>
     * </p>
     * @param accountId ID счета
     */
    void markPaymentsAsPaid(Long accountId);

}
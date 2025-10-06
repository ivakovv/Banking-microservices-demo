package org.example.account_processing.service;

import org.example.account_processing.model.Transaction;

import java.math.BigDecimal;

/**
 * @author Ivakov Andrey
 * Сервис для работы с транзакциями
 */
public interface TransactionService {

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод обрабатывает транзакцию </b> </i>
     * </p>
     * @param transaction транзакция для обработки
     * @return обработанная транзакция
     */
    Transaction processTransaction(Transaction transaction);


    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод создает график платежей для кредитного счета </b> </i>
     * </p>
     * @param accountId ID счета
     * @param amount сумма кредита
     * @param interestRate процентная ставка
     * @param termMonths срок в месяцах
     */
    void createPaymentSchedule(Long accountId, BigDecimal amount,
                               BigDecimal interestRate, int termMonths);

}

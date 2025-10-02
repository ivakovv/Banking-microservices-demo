package org.example.account_processing.service;

import org.example.account_processing.model.Account;

import java.time.LocalDateTime;

/**
 * @author Ivakov Andrey
 * Сервис для обработки ежемесячных платежей
 */
public interface MonthlyPaymentService {

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод обрабатывает ежемесячные платежи для кредитного счета</b> </i>
     * </p>
     * @param account кредитный счет
     * @param transactionDate дата транзакции
     */
    void processMonthlyPayments(Account account, LocalDateTime transactionDate);
}

package org.example.account_processing.service;

import org.example.account_processing.model.Account;

/**
 * @author Ivakov Andrey
 * Сервис для блокировки счетов
 */
public interface AccountBlockingService {

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод блокирует счет и все связанные с ним карты</b> </i>
     * </p>
     * @param account счет для блокировки
     * @param reason причина блокировки
     */
    void blockAccount(Account account, String reason);
}

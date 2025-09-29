package org.example.account_processing.service;

import org.example.account_processing.model.Account;

/**
 * @author Ivakov Andrey
 * Сервис для работы со счетами клиента
 */
public interface AccountService {

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод находит счет по ID</b> </i>
     * </p>
     * @param accountId ID счета
     * @return счет
     * @see Account
     */
    Account findById(Long accountId);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод создает счет из готового объекта</b> </i>
     * </p>
     * @param account готовый объект счета
     * @return созданный счет
     * @see Account
     */
    Account createAccount(Account account);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод сохраняет счет</b> </i>
     * </p>
     * @param account счет для обновления
     * @return обновленный счет
     * @see Account
     */
    Account saveAccount(Account account);
}

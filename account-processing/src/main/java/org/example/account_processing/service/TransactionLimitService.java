package org.example.account_processing.service;

/**
 * @author Ivakov Andrey
 * Сервис для работы с лимитами транзакций
 */
public interface TransactionLimitService {

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод проверяет, превышен ли лимит транзакций для карты</b> </i>
     * </p>
     * @param cardId ID карты
     * @return true если лимит превышен
     */
    boolean isLimitExceeded(Long cardId);
}

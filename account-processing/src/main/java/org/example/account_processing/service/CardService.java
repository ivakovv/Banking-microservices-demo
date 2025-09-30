package org.example.account_processing.service;

import org.example.account_processing.model.Card;

import java.util.List;

/**
 * @author Ivakov Andrey
 * Сервис для работы c картами
 */
public interface CardService {

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод создает карту </b> </i>
     * </p>
     * @param card готовый объект карты
     * @return созданная карта
     * @see Card
     */
    Card createCard(Card card);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для поиска карты по идентификатору </b> </i>
     * </p>
     * @param cardId идентификатор карты
     * @return полученная карта
     * @see Card
     */
    Card findById(String cardId);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод сохраняет карту </b> </i>
     * </p>
     * @param card карта для обновления
     * @return обновленный счет
     * @see Card
     */
    Card saveCard(Card card);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод находит все карты по счету </b> </i>
     * </p>
     * @param accountId идентификатор счета
     * @return список карт
     * @see Card
     */
    List<Card> findByAccountId(Long accountId);

}

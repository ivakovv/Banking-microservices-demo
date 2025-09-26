package org.example.account_processing.service;

import org.example.account_processing.model.Card;

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

}

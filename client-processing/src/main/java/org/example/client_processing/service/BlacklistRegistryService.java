package org.example.client_processing.service;

import org.example.client_processing.dto.blacklist.BlacklistRegistryRequest;
import org.example.client_processing.dto.blacklist.BlacklistRegistryResponse;
import org.example.client_processing.enums.client.DocumentType;

import java.time.LocalDateTime;


/**
 * @author Ivakov Andrey
 * Сервис для работы c заблокированными пользователями (их документами)
 */
public interface BlacklistRegistryService {

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для создания добавления в черный список</b> </i>
     * </p>
     * @param request: dto для добавления в черный список
     * @see BlacklistRegistryRequest
     * @return Возвращает сущность описывающий элемент черного списка
     * @see  BlacklistRegistryResponse
     */
    BlacklistRegistryResponse addToBlackList(BlacklistRegistryRequest request);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для удаления из черного списка</b> </i>
     * </p>
     * @param documentId: идентификатор документа
     */
    void deleteFromBlackList(String documentId);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для изменения даты окончания пребывания в черном списке</b> </i>
     * </p>
     * @param documentId: идентификатор документа
     * @param expirationDate: дата окончания блокировки
     * @return Возвращает сущность описывающий элемент черного списка
     * @see  BlacklistRegistryResponse
     */
    BlacklistRegistryResponse changeExpirationDate(String documentId, LocalDateTime expirationDate);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для получения сущности черного списка</b> </i>
     * </p>
     * @param documentId: идентификатор документа
     * @return Возвращает сущность описывающий элемент черного списка
     * @see  BlacklistRegistryResponse
     */
    BlacklistRegistryResponse getBlacklistRegistry(String documentId);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для проверки, заблокирован ли документ (с учетом дат)</b> </i>
     * </p>
     * @param documentType: тип документа
     * @param documentId: идентификатор документа
     * @return true если документ заблокирован, false если нет
     */
    boolean isBlacklisted(DocumentType documentType, String documentId);

}

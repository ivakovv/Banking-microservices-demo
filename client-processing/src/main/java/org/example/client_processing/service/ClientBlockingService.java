package org.example.client_processing.service;

/**
 * @author Ivakov Andrey
 * Сервис для блокировки и разблокировки клиентов.
 * Управляет изменением роли клиента при блокировке/разблокировке.
 */
public interface ClientBlockingService {

    /**
     * Блокирует клиента, изменяя его роль на BLOCKED_CLIENT.
     *
     * @param clientId идентификатор клиента
     * @param reason причина блокировки
     */
    void blockClient(String clientId, String reason);

    /**
     * Разблокирует клиента, возвращая его роль к CURRENT_CLIENT.
     * @see org.example.client_processing.enums.roles.UserRole
     * @param clientId идентификатор клиента
     */
    void unblockClient(String clientId);

    /**
     * Проверяет, заблокирован ли клиент.
     *
     * @param clientId идентификатор клиента
     * @return true, если клиент заблокирован
     */
    boolean isClientBlocked(String clientId);
}

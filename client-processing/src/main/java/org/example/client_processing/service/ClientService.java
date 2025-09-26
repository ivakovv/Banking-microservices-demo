package org.example.client_processing.service;

import org.example.client_processing.dto.client.ClientDto;
import org.example.client_processing.dto.client.RegistrationRequest;
import org.example.client_processing.dto.client.RegistrationResponse;
import org.example.client_processing.model.Client;

/**
 * @author Ivakov Andrey
 * Сервис для работы с клиентами
 */
public interface ClientService {

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для создания нового клиента с пользователем</b> </i>
     * </p>
     * @param request: dto для создания клиента и пользователя
     * @see RegistrationRequest
     * @return В случае успешного создания возвращает пользователя
     * @see  RegistrationResponse
     */
    RegistrationResponse register(RegistrationRequest request);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для получения клиента по ID</b> </i>
     * </p>
     * @param clientId ID клиента
     * @return клиент
     */
    ClientDto getClientById(String clientId);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для получения entity клиента по ID (для внутреннего использования)</b> </i>
     * </p>
     * @param clientId ID клиента
     * @return клиент entity
     */
    Client getClientEntityById(String clientId);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для получения валидированного клиента (включая проверку blacklist)</b> </i>
     * </p>
     * @param clientId ID клиента
     * @return валидированный клиент entity
     */
    Client getValidatedClient(String clientId);
}

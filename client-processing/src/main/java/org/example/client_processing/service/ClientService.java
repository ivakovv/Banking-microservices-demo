package org.example.client_processing.service;

import org.example.client_processing.dto.RegistrationRequest;
import org.example.client_processing.dto.RegistrationResponse;

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
}

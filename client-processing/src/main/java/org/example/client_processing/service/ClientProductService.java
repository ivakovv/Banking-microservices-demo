package org.example.client_processing.service;

import org.example.client_processing.dto.client_product.ClientProductRequest;
import org.example.client_processing.dto.client_product.ClientProductResponse;
import org.example.client_processing.dto.client_product.ReleaseCardRequest;
import org.example.client_processing.dto.client_product.ReleaseCardResponse;

import java.util.List;

/**
 * @author Ivakov Andrey
 * Сервис для работы с продуктами клиентов
 */
public interface ClientProductService {

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для создания нового продукта клиента</b> </i>
     * </p>
     * @param clientId ID клиента
     * @param productId ID продукта
     * @param request данные для создания продукта клиента
     * @see ClientProductRequest
     * @return созданный продукт клиента
     * @see ClientProductResponse
     */
    ClientProductResponse create(String clientId, String productId, ClientProductRequest request);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для получения продукта клиента по ID клиента и ID продукта</b> </i>
     * </p>
     * @param clientId ID клиента
     * @param productId ID продукта
     * @return продукт клиента
     * @see ClientProductResponse
     */
    ClientProductResponse getByClientIdAndProductId(String clientId, String productId);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для получения все продуктов клиента</b> </i>
     * </p>
     * @param clientId ID клиента
     * @return список продуктов клиента
     * @see ClientProductResponse
     */
    List<ClientProductResponse> getByClientId(String clientId);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для получения продуктов клиента по опр. типу</b> </i>
     * </p>
     * @param clientId ID клиента
     * @param productType тип продукта
     * @return список продуктов клиента
     * @see ClientProductResponse
     */
    List<ClientProductResponse> getByClientIdAndProductType(String clientId, String productType);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для обновления продукта клиента</b> </i>
     * </p>
     * @param clientId ID клиента
     * @param productId ID продукта
     * @param request данные для обновления
     * @see ClientProductRequest
     * @return обновленный продукт клиента
     * @see ClientProductResponse
     */
    ClientProductResponse updateByClientIdAndProductId(String clientId, String productId, ClientProductRequest request);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для удаления продукта клиента </b> </i>
     * </p>
     * @param clientId ID клиента
     * @param productId ID продукта
     */
    void deleteByClientIdAndProductId(String clientId, String productId);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для заявки выпуска карты </b> </i>
     * </p>
     * @param clientId ID клиента
     * @param request dto заявки для выпуска карты
     * @see ReleaseCardRequest
     * @return время и сообщение по заявке
     * @see ReleaseCardResponse
     */
    ReleaseCardResponse releaseCard(String clientId, ReleaseCardRequest request);

}
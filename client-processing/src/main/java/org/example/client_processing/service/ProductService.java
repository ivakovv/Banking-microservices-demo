package org.example.client_processing.service;

import org.example.client_processing.dto.product.ProductRequest;
import org.example.client_processing.dto.product.ProductResponse;
import org.example.client_processing.model.Product;

import java.util.List;

/**
 * @author Ivakov Andrey
 * Сервис для работы с клиентами
 */
public interface ProductService {

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для создания нового продукта</b> </i>
     * </p>
     * @param request: dto для создания продукта
     * @see ProductRequest
     * @return В случае успешного создания возвращает созданный продукт
     * @see  ProductResponse
     */
    ProductResponse create(ProductRequest request);
    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для получения продукта</b> </i>
     * </p>
     * @param productId: идентификатор продукта (key + id)
     * @return В случае существования - возвращает продукт по идентификатору
     * @see  ProductResponse
     */
    ProductResponse getByProductId(String productId);
    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для получения всех продуктов</b> </i>
     * </p>
     * @return Возвращает список всех продуктов
     * @see  ProductResponse
     */
    List<ProductResponse> getAll();
    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для обновления продукта</b> </i>
     * </p>
     * @param productId: идентификатор продукта (key + id)
     * @param request: dto для обновления продукта
     * @see ProductRequest
     * @return Возвращает обновленный продукт
     * @see  ProductResponse
     */
    ProductResponse update(String productId, ProductRequest request);
    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для удаления продукта</b> </i>
     * </p>
     * @param productId: идентификатор продукта (key + id)
     */
    void delete(String productId);

    /**
     * @author Ivakov Andrey
     * <p>
     *     <i> <b> Метод для получения продукта по productId</b> </i>
     * </p>
     * @param productId ID продукта
     * @return продукт
     * @throws IllegalArgumentException если продукт не найден
     */
    Product getProductByProductId(String productId);
}
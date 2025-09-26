package org.example.client_processing.mapper;

import org.example.client_processing.dto.product.ProductRequest;
import org.example.client_processing.dto.product.ProductResponse;
import org.example.client_processing.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "clientProducts", ignore = true)
    Product toEntity(ProductRequest product);

    ProductResponse toResponse(Product product);
    

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "createDate", source = "product.createDate")
    @Mapping(target = "clientProducts", source = "product.clientProducts")
    @Mapping(source = "request.name", target = "name")
    @Mapping(source = "request.key", target = "key")
    Product updateProduct(Product product, ProductRequest request);
}

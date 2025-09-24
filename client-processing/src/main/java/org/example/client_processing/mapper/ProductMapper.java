package org.example.client_processing.mapper;

import org.example.client_processing.dto.ProductRequest;
import org.example.client_processing.dto.ProductResponse;
import org.example.client_processing.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductRequest product);

    ProductResponse toResponse(Product product);
}

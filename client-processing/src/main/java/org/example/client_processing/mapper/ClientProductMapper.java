package org.example.client_processing.mapper;

import org.example.client_processing.dto.client_product.ClientProductRequest;
import org.example.client_processing.dto.client_product.ClientProductResponse;
import org.example.client_processing.model.Client;
import org.example.client_processing.model.ClientProduct;
import org.example.client_processing.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientProductMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", source = "client")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "openDate", source = "request.openDate")
    @Mapping(target = "closeDate", source = "request.closeDate")
    @Mapping(target = "status", source = "request.status")
    ClientProduct toEntity(ClientProductRequest request, Client client, Product product);
    
    @Mapping(target = "clientId", source = "client.clientId")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productType", source = "product.key")
    ClientProductResponse toResponse(ClientProduct clientProduct);
}

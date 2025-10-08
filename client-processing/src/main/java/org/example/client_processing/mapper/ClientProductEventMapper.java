package org.example.client_processing.mapper;

import org.example.client_processing.dto.client_product.ClientProductEventDto;
import org.example.client_processing.model.ClientProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientProductEventMapper {
    
    @Mapping(target = "clientProductId", source = "id")
    @Mapping(target = "clientId", source = "client.clientId")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productType", source = "product.key")
    @Mapping(target = "eventType", constant = "CLIENT_PRODUCT_CREATED")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "description", constant = "Client product created successfully")
    ClientProductEventDto toCreatedEvent(ClientProduct clientProduct);
    
    @Mapping(target = "clientProductId", source = "id")
    @Mapping(target = "clientId", source = "client.clientId")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productType", source = "product.key")
    @Mapping(target = "eventType", constant = "CLIENT_PRODUCT_UPDATED")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "description", constant = "Client product updated successfully")
    ClientProductEventDto toUpdatedEvent(ClientProduct clientProduct);
    
    @Mapping(target = "clientProductId", source = "id")
    @Mapping(target = "clientId", source = "client.clientId")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productType", source = "product.key")
    @Mapping(target = "eventType", constant = "CLIENT_PRODUCT_DELETED")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "description", constant = "Client product deleted successfully")
    ClientProductEventDto toDeletedEvent(ClientProduct clientProduct);
}

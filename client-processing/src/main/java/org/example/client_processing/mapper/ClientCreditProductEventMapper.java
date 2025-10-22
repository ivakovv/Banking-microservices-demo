package org.example.client_processing.mapper;

import org.example.client_processing.dto.client_product.ClientCreditProductEventDto;
import org.example.client_processing.dto.client_product.CreditInfoDto;
import org.example.client_processing.model.ClientProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientCreditProductEventMapper {

    @Mapping(target = "clientProductId", source = "clientProduct.id")
    @Mapping(target = "clientId", source = "clientProduct.client.clientId")
    @Mapping(target = "productId", source = "clientProduct.product.productId")
    @Mapping(target = "productName", source = "clientProduct.product.name")
    @Mapping(target = "productType", source = "clientProduct.product.key")
    @Mapping(target = "creditAmount", source = "creditInfoDto.creditAmount")
    @Mapping(target = "interestRate", source = "creditInfoDto.interestRate")
    @Mapping(target = "eventType", constant = "CLIENT_PRODUCT_CREATED")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "description", constant = "Client product created successfully")
    ClientCreditProductEventDto toCreatedEvent(ClientProduct clientProduct, CreditInfoDto creditInfoDto);

    @Mapping(target = "clientProductId", source = "clientProduct.id")
    @Mapping(target = "clientId", source = "clientProduct.client.clientId")
    @Mapping(target = "productId", source = "clientProduct.product.productId")
    @Mapping(target = "productName", source = "clientProduct.product.name")
    @Mapping(target = "productType", source = "clientProduct.product.key")
    @Mapping(target = "creditAmount", source = "creditInfoDto.creditAmount")
    @Mapping(target = "interestRate", source = "creditInfoDto.interestRate")
    @Mapping(target = "eventType", constant = "CLIENT_PRODUCT_UPDATED")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "description", constant = "Client product updated successfully")
    ClientCreditProductEventDto toUpdatedEvent(ClientProduct clientProduct, CreditInfoDto creditInfoDto);

    @Mapping(target = "clientProductId", source = "clientProduct.id")
    @Mapping(target = "clientId", source = "clientProduct.client.clientId")
    @Mapping(target = "productId", source = "clientProduct.product.productId")
    @Mapping(target = "productName", source = "clientProduct.product.name")
    @Mapping(target = "productType", source = "clientProduct.product.key")
    @Mapping(target = "creditAmount", source = "creditInfoDto.creditAmount")
    @Mapping(target = "interestRate", source = "creditInfoDto.interestRate")
    @Mapping(target = "eventType", constant = "CLIENT_PRODUCT_DELETED")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "description", constant = "Client product deleted successfully")
    ClientCreditProductEventDto toDeletedEvent(ClientProduct clientProduct, CreditInfoDto creditInfoDto);
}

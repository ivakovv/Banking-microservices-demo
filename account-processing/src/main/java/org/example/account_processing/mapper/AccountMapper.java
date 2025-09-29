package org.example.account_processing.mapper;

import org.example.account_processing.dto.product.ClientProductEventDto;
import org.example.account_processing.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "balance", constant = "0")
    @Mapping(target = "interestRate", ignore = true)
    @Mapping(target = "isRecalc", constant = "false")
    @Mapping(target = "cardExist", expression = "java(isCardProduct(eventDto.productType()))")
    @Mapping(target = "status", constant = "ACTIVE")
    Account toEntity(ClientProductEventDto eventDto);
    
    default boolean isCardProduct(String productType) {
        return "DC".equals(productType) || 
               "CC".equals(productType) || 
               "NS".equals(productType) || 
               "PENS".equals(productType);
    }
    
}

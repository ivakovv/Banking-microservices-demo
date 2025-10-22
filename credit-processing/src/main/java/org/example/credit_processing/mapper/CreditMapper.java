package org.example.credit_processing.mapper;

import org.example.credit_processing.dto.ClientCreditProductEventDto;
import org.example.credit_processing.model.ProductRegistry;
import org.example.credit_processing.util.TimeCalculationUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreditMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "interestRate", source = "interestRate")
    @Mapping(target = "monthCount", expression = "java(calculateMonths(source))")
    @Mapping(target = "amount", source = "creditAmount")
    @Mapping(target = "openDate", source = "openDate")
    @Mapping(target = "paymentRegistries", ignore = true)
    ProductRegistry toEntity(ClientCreditProductEventDto source);

    default Short calculateMonths(ClientCreditProductEventDto source) {
        Integer months = TimeCalculationUtil.calculateMonths(source.openDate(), source.closeDate());
        return months.shortValue();
    }
}

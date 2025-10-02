package org.example.account_processing.mapper;

import org.example.account_processing.dto.payment.PaymentEventDto;
import org.example.account_processing.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "paymentDate", source = "paymentEventDto.paymentDate")
    @Mapping(target = "amount", source = "paymentEventDto.amount")
    @Mapping(target = "isCredit", constant = "true")
    @Mapping(target = "type", constant = "DEPOSIT")
    @Mapping(target = "payedAt", source = "paymentEventDto.payedAt")
    @Mapping(target = "expired", constant = "false")
    Payment toEntity(PaymentEventDto paymentEventDto);
}
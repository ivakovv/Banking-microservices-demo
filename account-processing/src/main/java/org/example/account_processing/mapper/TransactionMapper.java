package org.example.account_processing.mapper;

import org.example.account_processing.dto.transaction.TransactionEventDto;
import org.example.account_processing.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "card", ignore = true)
    @Mapping(target = "type", source = "transactionEventDto.type")
    @Mapping(target = "amount", source = "transactionEventDto.amount")
    @Mapping(target = "status", constant = "ALLOWED")
    @Mapping(target = "timestamp", ignore = true)
    Transaction toEntity(TransactionEventDto transactionEventDto);
}

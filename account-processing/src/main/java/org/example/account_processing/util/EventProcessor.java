package org.example.account_processing.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.dto.product.ClientProductEventDto;
import org.example.account_processing.dto.card.ClientCardEventDto;
import org.example.account_processing.model.Account;
import org.example.account_processing.model.Card;
import org.example.account_processing.mapper.AccountMapper;
import org.example.account_processing.mapper.CardMapper;
import org.springframework.stereotype.Component;

/**
 * @author Ivakov Andrey
 * Утилита для обработки событий от Kafka
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventProcessor {

    private final AccountMapper accountMapper;
    private final CardMapper cardMapper;

    public Account processClientProductEvent(ClientProductEventDto event) {
        log.info("Processing client product event: {}", event);
        
        String eventType = event.eventType();
        
        if ("CLIENT_PRODUCT_CREATED".equals(eventType)) {
            return accountMapper.toEntity(event);
        } else {
            log.info("Skipping event type: {}", eventType);
            return null;
        }
    }

    public Card processClientCardEvent(ClientCardEventDto event) {
        log.info("Processing client card event: {}", event);
        
        String eventType = event.eventType();
        
        if ("CLIENT_CARD_CREATED".equals(eventType)) {
            return cardMapper.toEntity(event);
        } else {
            log.info("Skipping event type: {}", eventType);
            return null;
        }
    }
}
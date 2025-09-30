package org.example.account_processing.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.dto.product.ClientProductEventDto;
import org.example.account_processing.dto.card.ClientCardEventDto;
import org.example.account_processing.model.Account;
import org.example.account_processing.model.Card;
import org.example.account_processing.mapper.AccountMapper;
import org.example.account_processing.mapper.CardMapper;
import org.example.account_processing.service.AccountService;
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
    private final AccountService accountService;

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
            Card card = cardMapper.toEntity(event);

            Account account = accountService.findById(event.accountId());
            if (account == null) {
                log.error("Account not found with id: {}", event.accountId());
                return null;
            }
            if (!event.clientId().equals(account.getClientId())) {
                log.error("Client ID mismatch: event clientId={}, account clientId={}",
                        event.clientId(), account.getClientId());
                return null;
            }
            card.setAccount(account);

            return card;
        } else {
            log.info("Skipping event type: {}", eventType);
            return null;
        }
    }
}
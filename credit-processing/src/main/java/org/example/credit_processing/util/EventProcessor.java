package org.example.credit_processing.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.credit_processing.dto.ClientCreditProductEventDto;
import org.example.credit_processing.mapper.CreditMapper;
import org.example.credit_processing.model.ProductRegistry;
import org.springframework.stereotype.Component;

/**
 * @author Ivakov Andrey
 * Утилита для обработки событий от Kafka
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventProcessor {

    private final CreditMapper creditMapper;

    public ProductRegistry processClientProductEvent(ClientCreditProductEventDto event) {
        log.info("Processing client product event: {}", event);
        
        String eventType = event.eventType();
        
        if ("CLIENT_PRODUCT_CREATED".equals(eventType)) {
            return creditMapper.toEntity(event);
        } else {
            log.info("Skipping event type: {}", eventType);
            return null;
        }
    }
}
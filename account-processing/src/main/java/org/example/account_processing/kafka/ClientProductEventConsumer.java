package org.example.account_processing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.dto.product.ClientProductEventDto;
import org.example.account_processing.model.Account;
import org.example.account_processing.service.AccountService;
import org.example.account_processing.util.EventProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientProductEventConsumer {

    private final EventProcessor eventProcessor;
    private final AccountService accountService;

    @KafkaListener(topics = "${spring.kafka.topics.client-products}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleClientProductEvent(@Payload ClientProductEventDto event, 
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                       Acknowledgment ack) {
        try {
            log.info("Received client product event from topic {}: {}", topic, event);
            
            Account account = eventProcessor.processClientProductEvent(event);
            
            if (account != null) {
                accountService.createAccount(account);
                log.info("Successfully processed client product event");
            }
            
            ack.acknowledge();
            log.debug("Message acknowledged for client product event");
            
        } catch (Exception e) {
            log.error("Failed to process client product event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process client product event", e);
        }
    }
}

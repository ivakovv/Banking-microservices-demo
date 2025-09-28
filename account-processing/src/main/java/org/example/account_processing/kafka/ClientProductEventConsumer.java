package org.example.account_processing.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientProductEventConsumer {

    private final EventProcessor eventProcessor;
    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topics.client-products}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleClientProductEvent(@Payload Map<String, Object> eventMap, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                         Acknowledgment ack) {
        try {
            log.info("Received client product event from topic {}: {}", topic, eventMap);

            ClientProductEventDto event = objectMapper.convertValue(eventMap, ClientProductEventDto.class);

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

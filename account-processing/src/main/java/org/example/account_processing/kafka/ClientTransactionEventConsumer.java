package org.example.account_processing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientTransactionEventConsumer {

    @KafkaListener(topics = "${spring.kafka.topics.client-transactions}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleClientTransactionEvent(@Payload Object event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Received client transaction event from topic {}: {}", topic, event);
            
            // TODO: Analyze the event and process the transaction
            // This will be implemented when we create the TransactionService
            
            log.info("Successfully processed client transaction event");
        } catch (Exception e) {
            log.error("Failed to process client transaction event: {}", e.getMessage(), e);
            // TODO: Implement error handling and retry logic
        }
    }
}

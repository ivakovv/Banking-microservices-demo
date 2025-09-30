package org.example.account_processing.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.dto.transaction.TransactionEventDto;
import org.example.account_processing.model.Transaction;
import org.example.account_processing.service.TransactionService;
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
public class ClientTransactionEventConsumer {

    private final TransactionService transactionService;
    private final EventProcessor eventProcessor;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topics.client-transactions}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleClientTransactionEvent(@Payload Map<String, Object> eventMap,
                                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                             @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,
                                             Acknowledgment ack) {
        try {
            log.info("Received client transaction event from topic {} with key {}: {}", topic, messageKey, eventMap);

            TransactionEventDto event = objectMapper.convertValue(eventMap, TransactionEventDto.class);

            Transaction transaction = eventProcessor.processTransactionEvent(event);

            if (transaction != null) {
                transactionService.processTransaction(transaction);
                log.info("Successfully processed transaction for account {}", transaction.getAccount().getId());
            } else {
                log.info("Transaction event was not processed");
            }

            ack.acknowledge();
            log.debug("Message acknowledged for client transaction event");

        } catch (Exception e) {
            log.error("Failed to process client transaction event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process client transaction event", e);
        }
    }
}

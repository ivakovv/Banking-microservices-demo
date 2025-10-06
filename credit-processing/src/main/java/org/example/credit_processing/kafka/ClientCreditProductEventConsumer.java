package org.example.credit_processing.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.credit_processing.dto.ClientCreditProductEventDto;
import org.example.credit_processing.dto.CreditDecisionDto;
import org.example.credit_processing.model.ProductRegistry;
import org.example.credit_processing.service.CreditProcessingService;
import org.example.credit_processing.util.EventProcessor;
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
public class ClientCreditProductEventConsumer {

    private final ObjectMapper objectMapper;
    private final EventProcessor eventProcessor;
    private final CreditProcessingService creditProcessingService;
    private final DeadLetterQueueProducer deadLetterQueueProducer;

    @KafkaListener(topics = "${spring.kafka.topics.client-credit-products}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleClientCreditProductEvent(@Payload Map<String, Object> eventMap,
                                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                               Acknowledgment ack) {
        log.info("Received client credit product event from topic {}: {}", topic, eventMap);

        try {
            ClientCreditProductEventDto event = objectMapper.convertValue(eventMap, ClientCreditProductEventDto.class);
            log.info("Converted event: {}", event);

            ProductRegistry productRegistry = eventProcessor.processClientProductEvent(event);
            
            if (productRegistry == null) {
                log.info("Event type {} not supported, skipping", event.eventType());
                ack.acknowledge();
                return;
            }

            CreditDecisionDto decision = creditProcessingService.processCreditProduct(productRegistry);
            
            if (decision.approved()) {
                log.info("Credit approved for client: {}, creating payment schedule", event.clientId());

                creditProcessingService.createPaymentSchedule(productRegistry);
                
                log.info("Payment schedule created successfully for client: {}", event.clientId());
            } else {
                log.info("Credit rejected for client: {}, reason: {}", event.clientId(), decision.reason());
            }

            log.info("Successfully processed client credit product event");
            ack.acknowledge();
            log.debug("Message acknowledged for client credit product event");

        } catch (Exception e) {
            log.error("Failed to process client credit product event: {}", e.getMessage(), e);
            
            if (isRetryableError(e)) {
                log.warn("Retryable error occurred, message will be retried: {}", e.getMessage());
                throw new RuntimeException("Retryable error", e);
            } else {
                log.error("Non-retryable error occurred, sending to Dead Letter Queue: {}", e.getMessage());
                
                deadLetterQueueProducer.sendToDeadLetterQueue(topic, eventMap, e.getMessage(), e);
                
                ack.acknowledge();
            }
        }
    }

    private boolean isRetryableError(Exception e) {
        return e instanceof RuntimeException && 
               (e.getMessage().contains("Connection") || 
                e.getMessage().contains("Timeout") ||
                e.getMessage().contains("Service unavailable"));
    }
}

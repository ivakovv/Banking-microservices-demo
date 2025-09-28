package org.example.credit_processing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeadLetterQueueProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.client-credit-products-dlq}")
    private String dlqTopic;

    public void sendToDeadLetterQueue(String originalTopic, Map<String, Object> originalMessage, 
                                     String errorMessage, Exception exception) {
        try {
            Map<String, Object> dlqMessage = Map.of(
                "originalTopic", originalTopic,
                "originalMessage", originalMessage,
                "errorMessage", errorMessage,
                "exceptionType", exception.getClass().getSimpleName(),
                "timestamp", System.currentTimeMillis(),
                "retryCount", 0 
            );

            kafkaTemplate.send(dlqTopic, dlqMessage);
            log.warn("Message sent to Dead Letter Queue. Original topic: {}, Error: {}", 
                    originalTopic, errorMessage);

        } catch (Exception e) {
            log.error("Failed to send message to Dead Letter Queue: {}", e.getMessage(), e);
        }
    }
}

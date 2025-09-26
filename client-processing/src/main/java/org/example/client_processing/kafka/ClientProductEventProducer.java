package org.example.client_processing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client_processing.dto.client_product.ClientProductEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientProductEventProducer {

    private final KafkaTemplate<String, ClientProductEventDto> kafkaTemplate;

    @Value("${spring.kafka.topics.client-products:client_products}")
    private String clientProductsTopic;
    
    @Value("${spring.kafka.topics.client-credit-products:client_credit_products}")
    private String clientCreditProductsTopic;

    public void sendClientProductEvent(ClientProductEventDto clientProductEvent) {
        String topic = determineTopic(clientProductEvent.productType());
        
        try {
            kafkaTemplate.send(topic, clientProductEvent.clientId(), clientProductEvent);
            log.info("Successfully sent client product event to topic {}: clientId={}, productId={}, eventType={}", 
                    topic, clientProductEvent.clientId(), clientProductEvent.productId(), clientProductEvent.eventType());
        } catch (Exception e) {
            log.error("Failed to send client product event to topic {}: clientId={}, productId={}, error={}", 
                    topic, clientProductEvent.clientId(), clientProductEvent.productId(), e.getMessage(), e);
            throw new RuntimeException("Failed to send client product event", e);
        }
    }

    private String determineTopic(String productType) {
        return switch (productType) {
            case "DC", "CC", "NS", "PENS" -> clientProductsTopic;
            case "IPO", "PC", "AC" -> clientCreditProductsTopic;
            default -> {
                log.warn("Unknown product type: {}, sending to default topic", productType);
                yield clientProductsTopic;
            }
        };
    }
}

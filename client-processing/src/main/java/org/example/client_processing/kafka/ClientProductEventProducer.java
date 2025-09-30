package org.example.client_processing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client_processing.dto.client_product.ClientProductEventDto;
import org.example.client_processing.dto.client_product.ClientCreditProductEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientProductEventProducer {

    private final KafkaTemplate<String, ClientProductEventDto> kafkaTemplate;
    private final KafkaTemplate<String, ClientCreditProductEventDto> creditKafkaTemplate;

    @Value("${spring.kafka.topics.client-products:client_products}")
    private String clientProductsTopic;

    @Value("${spring.kafka.topics.client-credit-products:client_credit_products}")
    private String clientCreditProductsTopic;

    public void sendClientProductEvent(ClientProductEventDto clientProductEvent) {

        try {
            kafkaTemplate.send(clientProductsTopic, clientProductEvent.clientId(), clientProductEvent);
            log.info("Successfully sent client product event to topic {}: clientId={}, productId={}, eventType={}",
                    clientProductsTopic, clientProductEvent.clientId(), clientProductEvent.productId(), clientProductEvent.eventType());
        } catch (Exception e) {
            log.error("Failed to send client product event to topic {}: clientId={}, productId={}, error={}",
                    clientProductsTopic, clientProductEvent.clientId(), clientProductEvent.productId(), e.getMessage(), e);
            throw new RuntimeException("Failed to send client product event", e);
        }
    }

    public void sendCreditProductEvent(ClientCreditProductEventDto creditEvent) {
        try {
            creditKafkaTemplate.send(clientCreditProductsTopic, creditEvent.clientId(), creditEvent);
            log.info("Sent credit product event to {}: clientId={}, productType={}, amount={}",
                    clientCreditProductsTopic, creditEvent.clientId(), creditEvent.productType(), creditEvent.creditAmount());
        } catch (Exception e) {
            log.error("Failed to send credit product event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send credit product event", e);
        }
    }

}

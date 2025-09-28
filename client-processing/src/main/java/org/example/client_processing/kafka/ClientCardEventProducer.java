package org.example.client_processing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client_processing.dto.card.ClientCardEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientCardEventProducer {

    private final KafkaTemplate<String, ClientCardEventDto> kafkaTemplate;

    @Value("${spring.kafka.topics.client-cards:client_cards}")
    private String clientCardsTopic;

    public void sendCardCreationRequest(ClientCardEventDto cardEvent) {
        try {
            kafkaTemplate.send(clientCardsTopic, cardEvent.clientId(), cardEvent);
            log.info("Successfully sent card creation request to topic {}: clientId={}, accountId={}, eventType={}", 
                    clientCardsTopic, cardEvent.clientId(), cardEvent.accountId(), cardEvent.eventType());
        } catch (Exception e) {
            log.error("Failed to send card creation request to topic {}: clientId={}, accountId={}, error={}", 
                    clientCardsTopic, cardEvent.clientId(), cardEvent.accountId(), e.getMessage(), e);
            throw new RuntimeException("Failed to send card creation request", e);
        }
    }
}

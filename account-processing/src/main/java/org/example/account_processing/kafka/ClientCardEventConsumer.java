package org.example.account_processing.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.dto.card.ClientCardEventDto;
import org.example.account_processing.model.Card;
import org.example.account_processing.service.CardService;
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
public class ClientCardEventConsumer {

    private final EventProcessor eventProcessor;
    private final CardService cardService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topics.client-cards}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleClientCardEvent(@Payload Map<String, Object> eventMap, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                      Acknowledgment ack) {
        try {
            log.info("Received client card event from topic {}: {}", topic, eventMap);

            ClientCardEventDto event = objectMapper.convertValue(eventMap, ClientCardEventDto.class);

            Card card = eventProcessor.processClientCardEvent(event);

            if (card != null) {
                cardService.createCard(card);
                log.info("Successfully processed client card event");
            }

            ack.acknowledge();
            log.debug("Message acknowledged for client card event");

        } catch (Exception e) {
            log.error("Failed to process client card event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process client card event", e);
        }
    }
}

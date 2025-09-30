package org.example.account_processing.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.dto.payment.PaymentEventDto;
import org.example.account_processing.model.Payment;
import org.example.account_processing.service.PaymentService;
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
public class ClientPaymentEventConsumer {

    private final PaymentService paymentService;
    private final EventProcessor eventProcessor;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topics.client-payments}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleClientPaymentEvent(@Payload Map<String, Object> eventMap,
                                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                         @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,
                                         Acknowledgment ack) {
        try {
            log.info("Received client payment event from topic {} with key {}: {}", topic, messageKey, eventMap);

            PaymentEventDto event = objectMapper.convertValue(eventMap, PaymentEventDto.class);

            Payment payment = eventProcessor.processPaymentEvent(event);

            if (payment != null) {
                paymentService.processCreditPayment(payment);
                log.info("Successfully processed credit payment for account {}", payment.getAccount().getId());
            } else {
                log.info("Payment event was not processed");
            }

            ack.acknowledge();
            log.debug("Message acknowledged for client payment event");

        } catch (Exception e) {
            log.error("Failed to process client payment event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process client payment event", e);
        }
    }
}

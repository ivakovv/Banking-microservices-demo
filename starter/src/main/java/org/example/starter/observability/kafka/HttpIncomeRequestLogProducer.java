package org.example.starter.observability.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.starter.observability.dto.HttpIncomeRequestLogDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpIncomeRequestLogProducer {

    private static final Logger log = LoggerFactory.getLogger(HttpIncomeRequestLogProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public HttpIncomeRequestLogProducer(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Value("${spring.kafka.topics.service-logs}")
    private String serviceLogsTopic;

    @Value("${spring.application.name}")
    private String serviceName;

    public boolean sendHttpIncomeRequestLog(HttpIncomeRequestLogDto httpIncomeRequestLogDto) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(httpIncomeRequestLogDto);

            Message<String> message = MessageBuilder.withPayload(jsonMessage)
                    .setHeader(KafkaHeaders.TOPIC, serviceLogsTopic)
                    .setHeader("serviceName", serviceName)
                    .build();

            kafkaTemplate.send(message);
            log.info("Kafka: Sent HTTP income request log to topic {}: {}", serviceLogsTopic, jsonMessage);

            return true;

        } catch (JsonProcessingException e) {
            log.error("Kafka: Failed to serialize HTTP income request log: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Kafka: Failed to send HTTP income request log: {}", e.getMessage(), e);
            return false;
        }
    }
}



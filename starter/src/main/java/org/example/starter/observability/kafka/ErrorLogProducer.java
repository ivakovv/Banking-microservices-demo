package org.example.starter.observability.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.starter.observability.dto.ErrorLogDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class ErrorLogProducer {

    private static final Logger log = LoggerFactory.getLogger(ErrorLogProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.service-logs:service_logs}")
    private String serviceLogsTopic;

    @Value("${spring.application.name}")
    private String serviceName;

    public ErrorLogProducer(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public boolean sendErrorLog(ErrorLogDto errorLogDto) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(errorLogDto);
            
            Message<String> message = MessageBuilder
                    .withPayload(jsonMessage)
                    .setHeader(KafkaHeaders.TOPIC, serviceLogsTopic)
                    .setHeader(KafkaHeaders.KEY, serviceName)
                    .setHeader("type", errorLogDto.logLevel().name())
                    .setHeader("value", errorLogDto.logLevel().name())
                    .build();

            kafkaTemplate.send(message).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send error log to Kafka topic {}: {}", serviceLogsTopic, ex.getMessage());
                } else {
                    log.debug("Successfully sent error log to Kafka topic {}", serviceLogsTopic);
                }
            });
            
            return true;
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize error log to JSON: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Failed to send error log to Kafka: {}", e.getMessage());
            return false;
        }
    }

    public boolean sendErrorLogSync(ErrorLogDto errorLogDto) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(errorLogDto);
            
            Message<String> message = MessageBuilder
                    .withPayload(jsonMessage)
                    .setHeader(KafkaHeaders.TOPIC, serviceLogsTopic)
                    .setHeader(KafkaHeaders.KEY, serviceName)
                    .setHeader("type", errorLogDto.logLevel().name())
                    .setHeader("value", errorLogDto.logLevel().name())
                    .build();

            kafkaTemplate.send(message).get(); 
            log.debug("Successfully sent error log to Kafka topic {}", serviceLogsTopic);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send error log to Kafka: {}", e.getMessage());
            return false;
        }
    }
}

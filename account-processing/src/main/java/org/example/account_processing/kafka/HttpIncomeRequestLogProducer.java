package org.example.account_processing.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.dto.HttpIncomeRequestLogDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpIncomeRequestLogProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.service-logs}")
    private String serviceLogsTopic;

    @Value("${spring.application.name}")
    private String serviceName;

    public boolean sendHttpIncomeRequestLog(HttpIncomeRequestLogDto httpIncomeRequestLogDto) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(httpIncomeRequestLogDto);
            
            Message<String> message = MessageBuilder
                    .withPayload(jsonMessage)
                    .setHeader(KafkaHeaders.TOPIC, serviceLogsTopic)
                    .setHeader(KafkaHeaders.KEY, serviceName)
                    .setHeader("type", "INFO")
                    .setHeader("value", "INFO")
                    .build();

            kafkaTemplate.send(message).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send HTTP income request log to Kafka topic {}: {}", serviceLogsTopic, ex.getMessage());
                } else {
                    log.debug("Successfully sent HTTP income request log to Kafka topic {}", serviceLogsTopic);
                }
            });
            
            return true;
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize HTTP income request log to JSON: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Failed to send HTTP income request log to Kafka: {}", e.getMessage());
            return false;
        }
    }
}

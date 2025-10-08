package org.example.client_processing.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.client_processing.dto.card.ClientCardEventDto;
import org.example.client_processing.dto.client_product.ClientCreditProductEventDto;
import org.example.client_processing.dto.client_product.ClientProductEventDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.topics.client-products}")
    private String clientProductsTopic;
    
    @Value("${spring.kafka.topics.client-credit-products}")
    private String clientCreditProductsTopic;
    
    @Value("${spring.kafka.topics.client-cards}")
    private String clientCardsTopic;
    
    @Value("${spring.kafka.topics.service-logs}")
    private String serviceLogsTopic;

    @Value("${client-processing.kafka.producer.retries:3}")
    private Integer retries;
    
    @Value("${client-processing.kafka.producer.retry-backoff-ms:1000}")
    private Integer retryBackoffMs;
    
    @Value("${client-processing.kafka.producer.enable-idempotence:true}")
    private Boolean enableIdempotence;
    
    @Value("${client-processing.kafka.producer.acks:all}")
    private String acks;
    
    @Value("${client-processing.kafka.producer.batch-size:16384}")
    private Integer batchSize;
    
    @Value("${client-processing.kafka.producer.linger-ms:5}")
    private Integer lingerMs;
    
    @Value("${client-processing.kafka.producer.buffer-memory:33554432}")
    private Integer bufferMemory;


    @Bean("clientProductEventProducerFactory")
    public ProducerFactory<String, ClientProductEventDto> clientProductEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configProps.put(ProducerConfig.ACKS_CONFIG, acks);
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffMs);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);

        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);

        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        
        log.info("Creating Kafka Producer Factory with servers: {}, idempotence: {}, retries: {}", 
                bootstrapServers, enableIdempotence, retries);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }


    @Bean("clientProductEventTemplate")
    @Primary
    public KafkaTemplate<String, ClientProductEventDto> clientProductEventKafkaTemplate(
            @Qualifier("clientProductEventProducerFactory") ProducerFactory<String, ClientProductEventDto> producerFactory) {
        
        KafkaTemplate<String, ClientProductEventDto> template = new KafkaTemplate<>(producerFactory);

        template.setDefaultTopic(clientProductsTopic);
        
        log.info("Created KafkaTemplate with default topic: {}", clientProductsTopic);
        
        return template;
    }

    @Bean("clientCardEventTemplate")
    public KafkaTemplate<String, ClientCardEventDto> clientCardEventKafkaTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        configProps.put(ProducerConfig.ACKS_CONFIG, acks);
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffMs);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        
        ProducerFactory<String, ClientCardEventDto> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, ClientCardEventDto> template = new KafkaTemplate<>(producerFactory);
        template.setDefaultTopic(clientCardsTopic);
        
        log.info("Created ClientCard KafkaTemplate with topic: {}", clientCardsTopic);
        return template;
    }

    @Bean("clientCreditProductEventProducerFactory")
    public ProducerFactory<String, ClientCreditProductEventDto> clientCreditProductEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configProps.put(ProducerConfig.ACKS_CONFIG, acks);
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffMs);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);

        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);

        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        log.info("Creating Kafka Producer Factory for Credit Products with servers: {}, idempotence: {}, retries: {}",
                bootstrapServers, enableIdempotence, retries);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean("clientCreditProductEventTemplate")
    public KafkaTemplate<String, ClientCreditProductEventDto> clientCreditProductEventKafkaTemplate(
            @Qualifier("clientCreditProductEventProducerFactory") ProducerFactory<String, ClientCreditProductEventDto> producerFactory) {

        KafkaTemplate<String, ClientCreditProductEventDto> template = new KafkaTemplate<>(producerFactory);

        template.setDefaultTopic(clientCreditProductsTopic);

        log.info("Created Credit Product KafkaTemplate with default topic: {}", clientCreditProductsTopic);

        return template;
    }

    @Bean("kafkaTemplate")
    @Primary
    public KafkaTemplate<String, Object> kafkaTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        configProps.put(ProducerConfig.ACKS_CONFIG, acks);
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffMs);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        
        ProducerFactory<String, Object> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(producerFactory);
        
        log.info("Created general purpose KafkaTemplate for service logs and other messages");
        return template;
    }

    @Bean
    @ConditionalOnProperty(
            value = "client-processing.kafka.producer.enable",
            havingValue = "true",
            matchIfMissing = true
    )
    public String kafkaProducerStatus() {
        log.info("Kafka Producer is ENABLED for client-processing service");
        log.info("Available topics: client-products={}, client-credit-products={}, client-cards={}, service-logs={}",
                clientProductsTopic, clientCreditProductsTopic, clientCardsTopic, serviceLogsTopic);
        return "ENABLED";
    }
}

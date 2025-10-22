package org.example.client_processing.config;

import org.example.client_processing.kafka.ClientCardEventProducer;
import org.example.client_processing.kafka.ClientProductEventProducer;
import org.example.client_processing.service.BlacklistRegistryService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

/**
 * Тестовая конфигурация для интеграционных тестов
 */
@TestConfiguration
@Profile("test")
public class IntegrationTestConfiguration {

    @Bean
    @Primary
    public BlacklistRegistryService blacklistRegistryService() {
        return mock(BlacklistRegistryService.class);
    }

    @Bean
    @Primary
    public ClientCardEventProducer clientCardEventProducer() {
        return mock(ClientCardEventProducer.class);
    }

    @Bean
    @Primary
    public ClientProductEventProducer clientProductEventProducer() {
        return mock(ClientProductEventProducer.class);
    }
}

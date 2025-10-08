package org.example.starter.observability;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.starter.observability.aspect.HttpIncomeRequestLogAspect;
import org.example.starter.observability.aspect.HttpOutcomeRequestLogAspect;
import org.example.starter.observability.aspect.LogDatasourceErrorAspect;
import org.example.starter.observability.aspect.MetricAspect;
import org.example.starter.observability.kafka.ErrorLogProducer;
import org.example.starter.observability.kafka.HttpIncomeRequestLogProducer;
import org.example.starter.observability.kafka.HttpRequestLogProducer;
import org.example.starter.observability.kafka.MetricProducer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@AutoConfiguration
@EnableConfigurationProperties({MetricProperties.class, ErrorLogProperties.class, HttpRequestLogProperties.class})
public class ObservabilityAutoConfiguration {

    @Bean
    @ConditionalOnClass({KafkaTemplate.class, ObjectMapper.class})
    @ConditionalOnProperty(prefix = "observability.http-log", name = {"enabled", "income-enabled"}, havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public HttpIncomeRequestLogProducer httpIncomeRequestLogProducer(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        return new HttpIncomeRequestLogProducer(kafkaTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "observability.http-log", name = {"enabled", "income-enabled"}, havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public HttpIncomeRequestLogAspect httpIncomeRequestLogAspect(HttpIncomeRequestLogProducer producer) {
        return new HttpIncomeRequestLogAspect(producer);
    }

    @Bean
    @ConditionalOnClass({KafkaTemplate.class, ObjectMapper.class})
    @ConditionalOnProperty(prefix = "observability.metric", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public MetricProducer metricProducer(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        return new MetricProducer(kafkaTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "observability.metric", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public MetricAspect metricAspect(MetricProducer producer) {
        return new MetricAspect(producer);
    }

    @Bean
    @ConditionalOnClass({KafkaTemplate.class, ObjectMapper.class})
    @ConditionalOnProperty(prefix = "observability.error-log", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public ErrorLogProducer errorLogProducer(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        return new ErrorLogProducer(kafkaTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "observability.error-log", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public LogDatasourceErrorAspect logDatasourceErrorAspect(ErrorLogProducer producer) {
        return new LogDatasourceErrorAspect(producer);
    }

    @Bean
    @ConditionalOnClass({KafkaTemplate.class, ObjectMapper.class})
    @ConditionalOnProperty(prefix = "observability.http-request-log", name = {"enabled", "outcome-enabled"}, havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public HttpRequestLogProducer httpRequestLogProducer(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        return new HttpRequestLogProducer(kafkaTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "observability.http-request-log", name = {"enabled", "outcome-enabled"}, havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public HttpOutcomeRequestLogAspect httpOutcomeRequestLogAspect(HttpRequestLogProducer producer) {
        return new HttpOutcomeRequestLogAspect(producer);
    }
}



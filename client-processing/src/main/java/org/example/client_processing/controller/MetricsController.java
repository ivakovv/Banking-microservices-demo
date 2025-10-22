package org.example.client_processing.controller;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client_processing.service.BusinessMetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для предоставления бизнес-метрик системы.
 * Предоставляет REST API для получения метрик и статистики.
 */
@RestController
@RequestMapping("/client-processing/api/v1/metrics")
@RequiredArgsConstructor
@Slf4j
public class MetricsController {

    private final BusinessMetricsService businessMetricsService;
    private final MeterRegistry meterRegistry;

    
    @GetMapping("/products/statistics")
    public ResponseEntity<Map<String, Object>> getProductsStatistics() {
        log.info("Запрос статистики по банковским продуктам");
        
        Map<String, Object> statistics = businessMetricsService.getDetailedMetrics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "client-processing");
        response.put("statistics", statistics);
        
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/products/summary")
    public ResponseEntity<Map<String, Object>> getProductsSummary() {
        log.info("Запрос сводки по активным продуктам");
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("active_products_total", businessMetricsService.getActiveProductsCount());
        summary.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(summary);
    }

   
    @GetMapping(value = "/prometheus", produces = "text/plain")
    public ResponseEntity<String> getPrometheusMetrics() {
        log.debug("Запрос метрик Prometheus");
        
        StringBuilder metrics = new StringBuilder();
        
        metrics.append("# HELP banking_products_active_count Текущее количество активных банковских продуктов\n");
        metrics.append("# TYPE banking_products_active_count gauge\n");
        metrics.append("banking_products_active_count ").append(businessMetricsService.getActiveProductsCount()).append("\n\n");
        
        for (var productType : org.example.client_processing.enums.product.Key.values()) {
            double count = businessMetricsService.getActiveProductsCountByType(productType);
            metrics.append("# HELP banking_products_by_type_count Количество активных продуктов по типу\n");
            metrics.append("# TYPE banking_products_by_type_count gauge\n");
            metrics.append("banking_products_by_type_count{product_type=\"").append(productType.name()).append("\"} ").append(count).append("\n");
        }
        
        metrics.append("\n");
        
        for (var status : org.example.client_processing.enums.client_product.Status.values()) {
            double count = businessMetricsService.getProductsCountByStatus(status);
            metrics.append("# HELP banking_products_by_status_count Количество продуктов по статусу\n");
            metrics.append("# TYPE banking_products_by_status_count gauge\n");
            metrics.append("banking_products_by_status_count{status=\"").append(status.name()).append("\"} ").append(count).append("\n");
        }
        
        return ResponseEntity.ok(metrics.toString());
    }

    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("active_products", businessMetricsService.getActiveProductsCount());
        
        return ResponseEntity.ok(health);
    }
}
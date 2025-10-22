package org.example.client_processing.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client_processing.enums.client_product.Status;
import org.example.client_processing.enums.product.Key;
import org.example.client_processing.repository.ClientProductRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для сбора бизнес-метрик системы.
 * Предоставляет метрики для мониторинга состояния банковских продуктов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessMetricsService {

    private final MeterRegistry meterRegistry;
    private final ClientProductRepository clientProductRepository;

    private Counter productsOpenedCounter;
    private Counter productsClosedCounter;
    private Counter productsBlockedCounter;
    private Counter productsUnblockedCounter;
    
    private Timer productOpeningTimer;
    private Timer productClosingTimer;

    @PostConstruct
    public void initializeMetrics() {
        log.info("Инициализация бизнес-метрик для Prometheus");
        
        productsOpenedCounter = Counter.builder("banking.products.opened.total")
                .description("Общее количество открытых банковских продуктов")
                .register(meterRegistry);
                
        productsClosedCounter = Counter.builder("banking.products.closed.total")
                .description("Общее количество закрытых банковских продуктов")
                .register(meterRegistry);
                
        productsBlockedCounter = Counter.builder("banking.products.blocked.total")
                .description("Общее количество заблокированных банковских продуктов")
                .register(meterRegistry);
                
        productsUnblockedCounter = Counter.builder("banking.products.unblocked.total")
                .description("Общее количество разблокированных банковских продуктов")
                .register(meterRegistry);

        productOpeningTimer = Timer.builder("banking.products.opening.duration")
                .description("Время открытия банковского продукта")
                .register(meterRegistry);
                
        productClosingTimer = Timer.builder("banking.products.closing.duration")
                .description("Время закрытия банковского продукта")
                .register(meterRegistry);
        
        log.info("Бизнес-метрики успешно инициализированы");
    }

    /**
     * Получает общее количество активных продуктов
     */
    public double getActiveProductsCount() {
        return clientProductRepository.findAll().stream()
                .filter(cp -> cp.getStatus() == Status.ACTIVE)
                .count();
    }

    /**
     * Получает количество продуктов определенного типа
     */
    public double getProductsCountByType(Key productKey) {
        return clientProductRepository.findAll().stream()
                .filter(cp -> cp.getProduct().getKey() == productKey)
                .count();
    }

    /**
     * Получает количество продуктов с определенным статусом
     */
    public double getProductsCountByStatus(Status status) {
        return clientProductRepository.findAll().stream()
                .filter(cp -> cp.getStatus() == status)
                .count();
    }

    /**
     * Получает количество активных продуктов определенного типа
     */
    public double getActiveProductsCountByType(Key productKey) {
        return clientProductRepository.findAll().stream()
                .filter(cp -> cp.getProduct().getKey() == productKey && cp.getStatus() == Status.ACTIVE)
                .count();
    }

    /**
     * Уведомляет о открытии продукта
     */
    public void recordProductOpened(Key productType) {
        productsOpenedCounter.increment();
        log.debug("Зафиксировано открытие продукта типа: {}", productType);
    }

    /**
     * Уведомляет о закрытии продукта
     */
    public void recordProductClosed(Key productType) {
        productsClosedCounter.increment();
        log.debug("Зафиксировано закрытие продукта типа: {}", productType);
    }

    /**
     * Уведомляет о блокировке продукта
     */
    public void recordProductBlocked(Key productType) {
        productsBlockedCounter.increment();
        log.debug("Зафиксирована блокировка продукта типа: {}", productType);
    }

    /**
     * Уведомляет о разблокировке продукта
     */
    public void recordProductUnblocked(Key productType) {
        productsUnblockedCounter.increment();
        log.debug("Зафиксирована разблокировка продукта типа: {}", productType);
    }

    /**
     * Измеряет время открытия продукта
     */
    public Timer.Sample startProductOpeningTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * Завершает измерение времени открытия продукта
     */
    public void stopProductOpeningTimer(Timer.Sample sample, Key productType) {
        sample.stop(productOpeningTimer);
        log.debug("Зафиксировано время открытия продукта типа: {}", productType);
    }

    /**
     * Измеряет время закрытия продукта
     */
    public Timer.Sample startProductClosingTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * Завершает измерение времени закрытия продукта
     */
    public void stopProductClosingTimer(Timer.Sample sample, Key productType) {
        sample.stop(productClosingTimer);
        log.debug("Зафиксировано время закрытия продукта типа: {}", productType);
    }

    /**
     * Получает детальную статистику по продуктам
     */
    public Map<String, Object> getDetailedMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        
        // Общая статистика
        metrics.put("total_active_products", getActiveProductsCount());
        
        // Статистика по типам продуктов
        for (Key productKey : Key.values()) {
            metrics.put("active_" + productKey.name().toLowerCase(), getActiveProductsCountByType(productKey));
        }
        
        // Статистика по статусам
        for (Status status : Status.values()) {
            metrics.put("status_" + status.name().toLowerCase(), getProductsCountByStatus(status));
        }
        
        return metrics;
    }
}
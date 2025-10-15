package org.example.credit_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.credit_processing.dto.PaymentScheduleDto;
import org.example.credit_processing.model.PaymentRegistry;
import org.example.credit_processing.model.ProductRegistry;
import org.example.credit_processing.repository.ProductRegistryRepository;
import org.example.credit_processing.service.PaymentScheduleService;
import org.example.credit_processing.service.ProductRegistryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRegistryServiceImpl implements ProductRegistryService {

    private final ProductRegistryRepository productRegistryRepository;
    private final PaymentScheduleService paymentScheduleService;

    @Override
    @Transactional
    public ProductRegistry saveProductWithPaymentSchedule(ProductRegistry productRegistry, PaymentScheduleDto schedule) {
        log.info("Saving product registry: {}", productRegistry);

        // Сохраняем продукт
        ProductRegistry savedProduct = productRegistryRepository.save(productRegistry);
        log.info("Saved product registry with ID: {}", savedProduct.getId());

        // Создаем записи платежей
        if (schedule != null && schedule.payments() != null && !schedule.payments().isEmpty()) {
            List<PaymentRegistry> paymentRegistries = paymentScheduleService.createPaymentRegistries(savedProduct, schedule);
            
            // Добавляем платежи к продукту
            savedProduct.getPaymentRegistries().addAll(paymentRegistries);
            
            // Сохраняем обновленный продукт с платежами
            savedProduct = productRegistryRepository.save(savedProduct);
            
            log.info("Created {} payment registries for product: {}", 
                    paymentRegistries.size(), savedProduct.getId());
        }

        return savedProduct;
    }
}
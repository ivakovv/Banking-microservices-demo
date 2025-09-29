package org.example.account_processing.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.repository.CardRepository;
import org.springframework.stereotype.Component;

/**
 * @author Ivakov Andrey
 * Утилита для генерации номеров карт в российском формате
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CardNumberGenerator {

    private final CardRepository cardRepository;

    public String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            cardNumber.append((int) (Math.random() * 10));
        }

        String formatted = cardNumber.toString();
        return formatted.substring(0, 4) + " " +
                formatted.substring(4, 8) + " " +
                formatted.substring(8, 12) + " " +
                formatted.substring(12, 16);
    }

    /**
     * Генерирует уникальный номер карты с проверкой в базе данных
     * @return уникальный номер карты
     */
    public String generateUniqueCardNumber() {
        String cardNumber;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            cardNumber = generateCardNumber();
            attempts++;

            if (attempts > maxAttempts) {
                throw new RuntimeException("Failed to generate unique card number after " + maxAttempts + " attempts");
            }
        } while (cardRepository.existsByCardId(cardNumber));

        log.debug("Generated unique card number after {} attempts", attempts);
        return cardNumber;
    }
}

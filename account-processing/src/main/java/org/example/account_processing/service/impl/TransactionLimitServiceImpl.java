package org.example.account_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.repository.TransactionRepository;
import org.example.account_processing.service.TransactionLimitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionLimitServiceImpl implements TransactionLimitService {

    private final TransactionRepository transactionRepository;

    @Value("${transaction.limits.max-transactions:10}")
    private int maxTransactions;

    @Value("${transaction.limits.time-window-minutes:60}")
    private int timeWindowMinutes;

    @Override
    public boolean isLimitExceeded(Long cardId) {
        LocalDateTime startTime = LocalDateTime.now()
                .minus(timeWindowMinutes, ChronoUnit.MINUTES);
        
        long transactionCount = transactionRepository.countByCardIdAndTimestampAfter(cardId, startTime);
        
        boolean exceeded = transactionCount >= maxTransactions;
        
        if (exceeded) {
            log.warn("Transaction limit exceeded for card {}: {} >= {}", 
                    cardId, transactionCount, maxTransactions);
        }
        
        return exceeded;
    }
}

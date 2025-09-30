package org.example.account_processing.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.dto.payment.PaymentEventDto;
import org.example.account_processing.dto.product.ClientProductEventDto;
import org.example.account_processing.dto.card.ClientCardEventDto;
import org.example.account_processing.dto.transaction.TransactionEventDto;
import org.example.account_processing.mapper.PaymentMapper;
import org.example.account_processing.mapper.TransactionMapper;
import org.example.account_processing.model.Account;
import org.example.account_processing.model.Card;
import org.example.account_processing.mapper.AccountMapper;
import org.example.account_processing.mapper.CardMapper;
import org.example.account_processing.model.Payment;
import org.example.account_processing.model.Transaction;
import org.example.account_processing.service.AccountService;
import org.example.account_processing.service.CardService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Ivakov Andrey
 * Утилита для обработки событий от Kafka
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventProcessor {

    private final AccountMapper accountMapper;
    private final CardMapper cardMapper;
    private final AccountService accountService;
    private final PaymentMapper paymentMapper;
    private final TransactionMapper transactionMapper;
    private final CardService cardService;

    public Account processClientProductEvent(ClientProductEventDto event) {
        log.info("Processing client product event: {}", event);
        
        String eventType = event.eventType();
        
        if ("CLIENT_PRODUCT_CREATED".equals(eventType)) {
            return accountMapper.toEntity(event);
        } else {
            log.info("Skipping event type: {}", eventType);
            return null;
        }
    }

    public Card processClientCardEvent(ClientCardEventDto event) {
        log.info("Processing client card event: {}", event);

        String eventType = event.eventType();

        if ("CLIENT_CARD_CREATED".equals(eventType)) {
            Card card = cardMapper.toEntity(event);

            Account account = accountService.findById(event.accountId());
            if (account == null) {
                log.error("Account not found with id: {}", event.accountId());
                return null;
            }
            if (!event.clientId().equals(account.getClientId())) {
                log.error("Client ID mismatch: event clientId={}, account clientId={}",
                        event.clientId(), account.getClientId());
                return null;
            }
            card.setAccount(account);

            return card;
        } else {
            log.info("Skipping event type: {}", eventType);
            return null;
        }
    }

    public Payment processPaymentEvent(PaymentEventDto event) {
        log.info("Processing payment event: {}", event);

        Account account = accountService.findById(event.accountId());
        if (account == null) {
            log.error("Account not found with id: {}", event.accountId());
            return null;
        }

        if (event.amount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Payment amount {} must be positive for account {}",
                    event.amount(), event.accountId());
            return null;
        }

        BigDecimal debtAmount = account.getBalance().abs();
        if (event.amount().compareTo(debtAmount) > 0) {
            log.warn("Payment amount {} exceeds debt amount {} for account {}",
                    event.amount(), debtAmount, event.accountId());
            return null;
        }

        Payment payment = paymentMapper.toEntity(event);
        payment.setAccount(account);

        return payment;
    }

    public Transaction processTransactionEvent(TransactionEventDto event) {
        log.info("Processing transaction event: {}", event);

        Account account = accountService.findById(event.accountId());
        if (account == null) {
            log.error("Account not found with id: {}", event.accountId());
            return null;
        }

        Transaction transaction = transactionMapper.toEntity(event);
        transaction.setAccount(account);
        transaction.setTimestamp(java.time.LocalDateTime.now());

        if (event.cardId() != null) {
            Card card = cardService.findById(event.cardId());
            if (card != null) {
                transaction.setCard(card);
            }
        }

        return transaction;
    }
}
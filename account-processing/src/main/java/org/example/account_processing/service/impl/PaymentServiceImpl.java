package org.example.account_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.model.Account;
import org.example.account_processing.model.Payment;
import org.example.account_processing.repository.PaymentRepository;
import org.example.account_processing.service.AccountService;
import org.example.account_processing.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountService accountService;

    @Override
    @Transactional
    public Payment processCreditPayment(Payment payment) {

        Account account = payment.getAccount();

        if (!account.getIsRecalc()) {
            log.warn("Account {} is not a credit account", account.getId());
            throw new IllegalArgumentException("Account is not a credit account");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            log.warn("Account {} has no debt (balance: {})", account.getId(), account.getBalance());
            throw new IllegalArgumentException("Account has no debt");
        }

        BigDecimal debtAmount = account.getBalance().abs();
        if (payment.getAmount().compareTo(debtAmount) != 0) {
            log.warn("Payment amount {} does not match debt amount {} for account {}",
                    payment.getAmount(), debtAmount, account.getId());
            throw new IllegalArgumentException("Payment amount does not match debt amount");
        }

        BigDecimal newBalance = account.getBalance().add(payment.getAmount());
        account.setBalance(newBalance);
        accountService.saveAccount(account);

        log.info("Updated balance for account {}: {} -> {}",
                account.getId(), account.getBalance().subtract(payment.getAmount()), newBalance);

        Payment savedPayment = paymentRepository.save(payment);

        markPaymentsAsPaid(account.getId());

        log.info("Successfully processed credit payment for account {}", account.getId());
        return savedPayment;
    }

    @Override
    @Transactional
    public void markPaymentsAsPaid(Long accountId) {
        log.info("Marking payments as paid for account {}", accountId);
        int updatedCount = paymentRepository.updatePayedAtForAccount(accountId, LocalDateTime.now());
        log.info("Updated {} payments for account {}", updatedCount, accountId);
    }
}

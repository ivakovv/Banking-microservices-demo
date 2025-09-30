package org.example.account_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.model.Account;
import org.example.account_processing.model.Payment;
import org.example.account_processing.repository.PaymentRepository;
import org.example.account_processing.service.AccountService;
import org.example.account_processing.service.MonthlyPaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class MonthlyPaymentServiceImpl implements MonthlyPaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountService accountService;

    @Override
    @Transactional
    public void processMonthlyPayments(Account account, LocalDateTime transactionDate) {
        log.info("Processing monthly payments for account {} on date {}", 
                account.getId(), transactionDate);

        List<Payment> duePayments = paymentRepository.findDuePaymentsByAccountId(
                account.getId(), transactionDate);

        for (Payment payment : duePayments) {
            processPayment(account, payment, transactionDate);
        }
    }

    private void processPayment(Account account, Payment payment, LocalDateTime transactionDate) {
        if (hasSufficientFunds(account, payment)) {
            deductPayment(account, payment, transactionDate);
        } else {
            markPaymentAsExpired(payment);
        }
    }

    private boolean hasSufficientFunds(Account account, Payment payment) {
        return account.getBalance().compareTo(payment.getAmount()) >= 0;
    }

    private void deductPayment(Account account, Payment payment, LocalDateTime transactionDate) {
        account.setBalance(account.getBalance().subtract(payment.getAmount()));
        accountService.saveAccount(account);

        payment.setPayedAt(transactionDate);
        paymentRepository.save(payment);

        log.info("Processed payment {} for account {}, new balance: {}", 
                payment.getAmount(), account.getId(), account.getBalance());
    }

    @Transactional
    private void markPaymentAsExpired(Payment payment) {
        payment.setExpired(true);
        paymentRepository.save(payment);

        log.warn("Payment {} expired due to insufficient funds", payment.getId());
    }
}

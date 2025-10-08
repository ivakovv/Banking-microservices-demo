package org.example.account_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.enums.Type;
import org.example.account_processing.enums.transaction.Status;
import org.example.account_processing.model.Account;
import org.example.account_processing.model.Payment;
import org.example.account_processing.model.Transaction;
import org.example.account_processing.repository.PaymentRepository;
import org.example.account_processing.repository.TransactionRepository;
import org.example.account_processing.service.AccountBlockingService;
import org.example.account_processing.service.AccountService;
import org.example.account_processing.service.MonthlyPaymentService;
import org.example.account_processing.service.TransactionLimitService;
import org.example.account_processing.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final AccountService accountService;
    private final TransactionLimitService transactionLimitService;
    private final AccountBlockingService accountBlockingService;
    private final MonthlyPaymentService monthlyPaymentService;

    @Override
    @Transactional
    public Transaction processTransaction(Transaction transaction) {

        Account account = transaction.getAccount();

        if (!accountService.isAccountActive(account)) {
            log.error("Inactive account with id {} status {}", account.getId(), account.getStatus());
            throw new IllegalArgumentException("Inactive account with id " + account.getId());
        }

        if (transaction.getCard() != null && transactionLimitService.isLimitExceeded(transaction.getCard().getId())) {
            log.warn("Transaction limit exceeded for card {}", transaction.getCard().getId());
            accountBlockingService.blockAccount(account, "Transaction limit exceeded");
            transaction.setStatus(Status.BLOCKED);
            return transactionRepository.save(transaction);
        }

        updateBalanceByTransaction(account, transaction.getAmount(), transaction.getType());

        if (account.getIsRecalc() && transaction.getType() == Type.DEPOSIT) {
            createPaymentSchedule(account.getId(), transaction.getAmount(), 
                               account.getInterestRate(), 12);
        }

        if (account.getIsRecalc() && transaction.getType() == Type.DEPOSIT) {
            monthlyPaymentService.processMonthlyPayments(account, transaction.getTimestamp());
        }

        return transactionRepository.save(transaction);

    }

    @Override
    @Transactional
    public void createPaymentSchedule(Long accountId, BigDecimal amount, BigDecimal interestRate, int termMonths) {

        Account account = accountService.findById(accountId);

        BigDecimal monthlyPayment = calculateMonthlyPayment(amount, interestRate, termMonths);
        
        LocalDateTime currentDate = LocalDateTime.now();
        
        for (int month = 1; month <= termMonths; month++) {
            Payment payment = new Payment();
            payment.setAccount(account);
            payment.setAmount(monthlyPayment);
            payment.setPaymentDate(currentDate.plusMonths(month));
            payment.setIsCredit(true);
            payment.setType(Type.DEPOSIT);
            payment.setExpired(false);
            
            paymentRepository.save(payment);
            
            log.info("Created payment {} for account {} due on {}", 
                    monthlyPayment, accountId, payment.getPaymentDate());
        }
    }

    private void updateBalanceByTransaction(Account account, BigDecimal amount, Type type) {
        switch (type) {
            case DEPOSIT -> account.setBalance(account.getBalance().add(amount));
            case WITHDRAW -> account.setBalance(account.getBalance().subtract(amount));
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    /**
     * Рассчитывает ежемесячный платеж по формуле аннуитета
     * Формула: А = S × [i × (1 + i)^n] / [(1 + i)^n - 1]
     * Где:
     * А — размер аннуитетного взноса
     * S — сумма кредита
     * i — месячная процентная ставка (годовая ставка / 12)
     * n — количество периодов (месяцев)
     * 
     * @param principal основная сумма кредита (S)
     * @param annualRate годовая процентная ставка в процентах
     * @param months количество месяцев (n)
     * @return ежемесячный платеж (А)
     */
    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal annualRate, int months) {
        if (annualRate == null || annualRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP)
                                          .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRateToN = onePlusRate.pow(months);
        
        BigDecimal numerator = monthlyRate.multiply(onePlusRateToN);
        BigDecimal denominator = onePlusRateToN.subtract(BigDecimal.ONE);
        
        return principal.multiply(numerator).divide(denominator, 2, RoundingMode.HALF_UP);
    }

}
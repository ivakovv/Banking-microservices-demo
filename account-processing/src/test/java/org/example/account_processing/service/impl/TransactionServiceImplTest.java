package org.example.account_processing.service.impl;

import org.example.account_processing.enums.Type;
import org.example.account_processing.enums.transaction.Status;
import org.example.account_processing.model.Account;
import org.example.account_processing.model.Card;
import org.example.account_processing.model.Payment;
import org.example.account_processing.model.Transaction;
import org.example.account_processing.repository.PaymentRepository;
import org.example.account_processing.repository.TransactionRepository;
import org.example.account_processing.service.AccountBlockingService;
import org.example.account_processing.service.AccountService;
import org.example.account_processing.service.MonthlyPaymentService;
import org.example.account_processing.service.TransactionLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionLimitService transactionLimitService;

    @Mock
    private AccountBlockingService accountBlockingService;

    @Mock
    private MonthlyPaymentService monthlyPaymentService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account testAccount;
    private Transaction testTransaction;
    private Card testCard;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setClientId("770100000001");
        testAccount.setProductId("DC1");
        testAccount.setBalance(BigDecimal.valueOf(25000.00));
        testAccount.setInterestRate(BigDecimal.valueOf(7.5));
        testAccount.setIsRecalc(true);
        testAccount.setCardExist(true);
        testAccount.setStatus(org.example.account_processing.enums.Status.ACTIVE);

        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardId("1234567890123456");
        testCard.setPaymentSystem("VISA");
        testCard.setStatus(org.example.account_processing.enums.Status.ACTIVE);

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAccount(testAccount);
        testTransaction.setCard(testCard);
        testTransaction.setType(Type.DEPOSIT);
        testTransaction.setAmount(BigDecimal.valueOf(15000.00));
        testTransaction.setStatus(Status.PROCESSING);
        testTransaction.setTimestamp(LocalDateTime.now());
    }

    @Test
    void processTransaction_WithValidData_ShouldProcessSuccessfully() {
        // Given
        when(accountService.isAccountActive(testAccount)).thenReturn(true);
        when(transactionLimitService.isLimitExceeded(1L)).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        Transaction result = transactionService.processTransaction(testTransaction);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(40000.00), testAccount.getBalance());
        verify(accountService).isAccountActive(testAccount);
        verify(transactionLimitService).isLimitExceeded(1L);
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void processTransaction_WithInactiveAccount_ShouldThrowException() {
        // Given
        when(accountService.isAccountActive(testAccount)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transactionService.processTransaction(testTransaction)
        );

        assertEquals("Inactive account with id 1", exception.getMessage());
        verify(accountService).isAccountActive(testAccount);
        verify(transactionLimitService, never()).isLimitExceeded(anyLong());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void processTransaction_WithLimitExceeded_ShouldBlockAccount() {
        // Given
        when(accountService.isAccountActive(testAccount)).thenReturn(true);
        when(transactionLimitService.isLimitExceeded(1L)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        Transaction result = transactionService.processTransaction(testTransaction);

        // Then
        assertNotNull(result);
        assertEquals(Status.BLOCKED, result.getStatus());
        verify(accountBlockingService).blockAccount(testAccount, "Transaction limit exceeded");
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void processTransaction_WithWithdrawType_ShouldDecreaseBalance() {
        // Given
        testTransaction.setType(Type.WITHDRAW);
        testTransaction.setAmount(BigDecimal.valueOf(5000.00));
        when(accountService.isAccountActive(testAccount)).thenReturn(true);
        when(transactionLimitService.isLimitExceeded(1L)).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        Transaction result = transactionService.processTransaction(testTransaction);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(20000.00), testAccount.getBalance());
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void processTransaction_WithDepositAndRecalc_ShouldCreatePaymentSchedule() {
        // Given
        when(accountService.isAccountActive(testAccount)).thenReturn(true);
        when(transactionLimitService.isLimitExceeded(1L)).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());

        // When
        Transaction result = transactionService.processTransaction(testTransaction);

        // Then
        assertNotNull(result);
        verify(paymentRepository, times(12)).save(any(Payment.class));
        verify(monthlyPaymentService).processMonthlyPayments(testAccount, testTransaction.getTimestamp());
    }

    @Test
    void createPaymentSchedule_WithValidData_ShouldCreatePayments() {
        // Given
        Long accountId = 1L;
        BigDecimal amount = BigDecimal.valueOf(10000.00);
        BigDecimal interestRate = BigDecimal.valueOf(12.0);
        int termMonths = 12;
        
        when(accountService.findById(accountId)).thenReturn(testAccount);
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());

        // When
        transactionService.createPaymentSchedule(accountId, amount, interestRate, termMonths);

        // Then
        verify(accountService).findById(accountId);
        verify(paymentRepository, times(12)).save(any(Payment.class));
    }

    @Test
    void createPaymentSchedule_WithZeroInterestRate_ShouldCalculateEqualPayments() {
        // Given
        Long accountId = 1L;
        BigDecimal amount = BigDecimal.valueOf(12000.00);
        BigDecimal interestRate = BigDecimal.ZERO;
        int termMonths = 12;
        
        when(accountService.findById(accountId)).thenReturn(testAccount);
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());

        // When
        transactionService.createPaymentSchedule(accountId, amount, interestRate, termMonths);

        // Then
        verify(paymentRepository, times(12)).save(any(Payment.class));
    }

    @Test
    void processTransaction_WithUnknownType_ShouldThrowException() {
        // Given
        testTransaction.setType(null);
        when(accountService.isAccountActive(testAccount)).thenReturn(true);
        when(transactionLimitService.isLimitExceeded(1L)).thenReturn(false);

        // When & Then
        assertThrows(
            NullPointerException.class,
            () -> transactionService.processTransaction(testTransaction)
        );
    }
}
package org.example.account_processing.service.impl;

import org.example.account_processing.enums.Type;
import org.example.account_processing.model.Account;
import org.example.account_processing.model.Payment;
import org.example.account_processing.repository.PaymentRepository;
import org.example.account_processing.service.AccountService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Account testAccount;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setClientId("770200000002");
        testAccount.setProductId("AC1");
        testAccount.setBalance(BigDecimal.valueOf(-50000.00)); // Отрицательный баланс = долг
        testAccount.setInterestRate(BigDecimal.valueOf(15.0));
        testAccount.setIsRecalc(true); // Кредитный сче
        testAccount.setCardExist(true);
        testAccount.setStatus(org.example.account_processing.enums.Status.ACTIVE);

        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setAccount(testAccount);
        testPayment.setAmount(BigDecimal.valueOf(50000.00)); // Сумма платежа = размер долга
        testPayment.setPaymentDate(LocalDateTime.now());
        testPayment.setIsCredit(true);
        testPayment.setType(Type.DEPOSIT);
        testPayment.setPayedAt(null); 
    }

    @Test
    void processCreditPayment_WithValidData_ShouldProcessSuccessfully() {
        // Given
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(accountService.saveAccount(any(Account.class))).thenReturn(testAccount);
        when(paymentRepository.updatePayedAtForAccount(anyLong(), any(LocalDateTime.class))).thenReturn(1);

        // When
        Payment result = paymentService.processCreditPayment(testPayment);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO.setScale(1), testAccount.getBalance()); // Долг погашен
        verify(paymentRepository).save(testPayment);
        verify(accountService).saveAccount(testAccount);
        verify(paymentRepository).updatePayedAtForAccount(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void processCreditPayment_WithNonCreditAccount_ShouldThrowException() {
        // Given
        testAccount.setIsRecalc(false); // Не кредитный счет

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> paymentService.processCreditPayment(testPayment)
        );

        assertEquals("Account is not a credit account", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processCreditPayment_WithNoDebt_ShouldThrowException() {
        // Given
        testAccount.setBalance(BigDecimal.valueOf(1000.00)); // Положительный баланс = нет долга

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> paymentService.processCreditPayment(testPayment)
        );

        assertEquals("Account has no debt", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void markPaymentsAsPaid_WithValidAccountId_ShouldMarkPaymentsAsPaid() {
        // Given
        Long accountId = 1L;
        when(paymentRepository.updatePayedAtForAccount(eq(accountId), any(LocalDateTime.class))).thenReturn(3);

        // When
        paymentService.markPaymentsAsPaid(accountId);

        // Then
        verify(paymentRepository).updatePayedAtForAccount(eq(accountId), any(LocalDateTime.class));
    }

    @Test
    void markPaymentsAsPaid_WithNoUnpaidPayments_ShouldDoNothing() {
        // Given
        Long accountId = 1L;
        when(paymentRepository.updatePayedAtForAccount(eq(accountId), any(LocalDateTime.class))).thenReturn(0);

        // When
        paymentService.markPaymentsAsPaid(accountId);

        // Then
        verify(paymentRepository).updatePayedAtForAccount(eq(accountId), any(LocalDateTime.class));
    }
}
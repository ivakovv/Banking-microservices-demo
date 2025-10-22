package org.example.credit_processing.service.impl;

import org.example.credit_processing.dto.AccountDto;
import org.example.credit_processing.dto.CreditDecisionDto;
import org.example.credit_processing.dto.PaymentScheduleDto;
import org.example.credit_processing.mapper.CreditDecisionMapper;
import org.example.credit_processing.model.ProductRegistry;
import org.example.credit_processing.service.CreditDecisionService;
import org.example.credit_processing.service.PaymentScheduleService;
import org.example.credit_processing.service.ProductRegistryService;
import org.example.credit_processing.service.info.AccountInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditProcessingServiceImplTest {

    @Mock
    private CreditDecisionService creditDecisionService;

    @Mock
    private PaymentScheduleService paymentScheduleService;

    @Mock
    private ProductRegistryService productRegistryService;

    @Mock
    private AccountInfoService accountInfoService;

    @Mock
    private CreditDecisionMapper creditDecisionMapper;

    @InjectMocks
    private CreditProcessingServiceImpl creditProcessingService;

    private ProductRegistry testProductRegistry;
    private CreditDecisionDto approvedDecision;
    private CreditDecisionDto rejectedDecision;
    private PaymentScheduleDto paymentSchedule;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        testProductRegistry = new ProductRegistry();
        testProductRegistry.setId(1L);
        testProductRegistry.setClientId("770300000003");
        testProductRegistry.setProductId("AC1");
        testProductRegistry.setInterestRate(BigDecimal.valueOf(18.5));
        testProductRegistry.setMonthCount((short) 24);
        testProductRegistry.setAmount(BigDecimal.valueOf(500000.00));
        testProductRegistry.setOpenDate(LocalDateTime.now());

        approvedDecision = new CreditDecisionDto(
                "770300000003",
                "AC1", 
                BigDecimal.valueOf(500000.00), 
                BigDecimal.valueOf(0.00), 
                BigDecimal.valueOf(1000000.00),
                false,
                true,
                "Credit approved",
                BigDecimal.valueOf(25000.00),
                BigDecimal.valueOf(600000.00),
                BigDecimal.valueOf(100000.00)
        );

        rejectedDecision = new CreditDecisionDto(
                "770300000003",
                "AC1",
                BigDecimal.valueOf(500000.00), 
                BigDecimal.valueOf(0.00), 
                BigDecimal.valueOf(0.00),
                false,
                false,
                "Insufficient income",
                BigDecimal.valueOf(0.00),
                BigDecimal.valueOf(0.00),
                BigDecimal.valueOf(0.00)
        );

        paymentSchedule = new PaymentScheduleDto(
                1L,
                BigDecimal.valueOf(500000.00),
                BigDecimal.valueOf(25000.00),
                BigDecimal.valueOf(600000.00),
                BigDecimal.valueOf(100000.00),
                List.of()
        );

        accountDto = new AccountDto(1L, "770300000003", "AC1", BigDecimal.valueOf(500000.00), 
                BigDecimal.valueOf(18.5), true, true, "ACTIVE");
    }

    @Test
    void processCreditProduct_WithApprovedCredit_ShouldProcessSuccessfully() {
        // Given
        when(creditDecisionService.makeCreditDecision(testProductRegistry)).thenReturn(approvedDecision);
        when(accountInfoService.getAccountByClientAndProductId("770300000003", "AC1")).thenReturn(accountDto);
        when(paymentScheduleService.createPaymentSchedule(testProductRegistry)).thenReturn(paymentSchedule);
        when(productRegistryService.saveProductWithPaymentSchedule(testProductRegistry, paymentSchedule))
                .thenReturn(testProductRegistry);
        when(creditDecisionMapper.createApprovalDecisionWithPayments(
                any(ProductRegistry.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(Boolean.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class)
        )).thenReturn(approvedDecision);

        // When
        CreditDecisionDto result = creditProcessingService.processCreditProduct(testProductRegistry);

        // Then
        assertNotNull(result);
        assertTrue(result.approved());
        assertEquals(1L, testProductRegistry.getAccountId());
        
        verify(creditDecisionService).makeCreditDecision(testProductRegistry);
        verify(accountInfoService).getAccountByClientAndProductId("770300000003", "AC1");
        verify(paymentScheduleService).createPaymentSchedule(testProductRegistry);
        verify(productRegistryService).saveProductWithPaymentSchedule(testProductRegistry, paymentSchedule);
        verify(creditDecisionMapper).createApprovalDecisionWithPayments(
                any(ProductRegistry.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(Boolean.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class)
        );
    }

    @Test
    void processCreditProduct_WithRejectedCredit_ShouldReturnRejection() {
        // Given
        when(creditDecisionService.makeCreditDecision(testProductRegistry)).thenReturn(rejectedDecision);

        // When
        CreditDecisionDto result = creditProcessingService.processCreditProduct(testProductRegistry);

        // Then
        assertNotNull(result);
        assertFalse(result.approved());
        assertEquals("Insufficient income", result.reason());
        
        verify(creditDecisionService).makeCreditDecision(testProductRegistry);
        verify(accountInfoService, never()).getAccountByClientAndProductId(anyString(), anyString());
        verify(paymentScheduleService, never()).createPaymentSchedule(any(ProductRegistry.class));
        verify(productRegistryService, never()).saveProductWithPaymentSchedule(any(), any());
    }

    @Test
    void createPaymentSchedule_WithValidData_ShouldCreateSchedule() {
        // Given
        when(accountInfoService.getAccountByClientAndProductId("770300000003", "AC1")).thenReturn(accountDto);
        when(paymentScheduleService.createPaymentSchedule(testProductRegistry)).thenReturn(paymentSchedule);

        // When
        PaymentScheduleDto result = creditProcessingService.createPaymentSchedule(testProductRegistry);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(25000.00), result.monthlyPayment());
        assertEquals(BigDecimal.valueOf(600000.00), result.totalPayment());
        assertEquals(BigDecimal.valueOf(100000.00), result.overpayment());
        
        assertEquals(1L, testProductRegistry.getAccountId());
        verify(accountInfoService).getAccountByClientAndProductId("770300000003", "AC1");
        verify(paymentScheduleService).createPaymentSchedule(testProductRegistry);
    }

    @Test
    void processCreditProduct_WithOverduePayments_ShouldProcessWithOverdueFlag() {
        // Given
        CreditDecisionDto decisionWithOverdue = new CreditDecisionDto(
                "770300000003",
                "AC1",
                BigDecimal.valueOf(30000.00), 
                BigDecimal.valueOf(80000.00), 
                BigDecimal.valueOf(1000000.00),
                true,
                true,
                "Credit approved with overdue payments",
                BigDecimal.valueOf(9000.00),
                BigDecimal.valueOf(108000.00),
                BigDecimal.valueOf(8000.00)
        );
        
        when(creditDecisionService.makeCreditDecision(testProductRegistry)).thenReturn(decisionWithOverdue);
        when(accountInfoService.getAccountByClientAndProductId("770300000003", "AC1")).thenReturn(accountDto);
        when(paymentScheduleService.createPaymentSchedule(testProductRegistry)).thenReturn(paymentSchedule);
        when(productRegistryService.saveProductWithPaymentSchedule(testProductRegistry, paymentSchedule))
                .thenReturn(testProductRegistry);
        when(creditDecisionMapper.createApprovalDecisionWithPayments(
                any(ProductRegistry.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(Boolean.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class)
        )).thenReturn(decisionWithOverdue);

        // When
        CreditDecisionDto result = creditProcessingService.processCreditProduct(testProductRegistry);

        // Then
        assertNotNull(result);
        assertTrue(result.approved());
        assertTrue(result.hasOverduePayments());
        
        verify(creditDecisionMapper).createApprovalDecisionWithPayments(
                any(ProductRegistry.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(Boolean.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class)
        );
    }
}
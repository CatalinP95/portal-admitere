package com.campus.dormitory.service;

import com.campus.dormitory.exception.BadRequestException;
import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.messaging.EventPublisher;
import com.campus.dormitory.model.PaymentStatus;
import com.campus.dormitory.model.Receipt;
import com.campus.dormitory.repository.jpa.ReceiptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @Mock private ReceiptRepository receiptRepository;
    @Mock private AuditLogService auditLogService;
    @Mock private EventPublisher eventPublisher;

    @InjectMocks private ReceiptService receiptService;

    private Receipt unpaid;

    @BeforeEach
    void setUp() {
        unpaid = new Receipt();
        unpaid.setId(1);
        unpaid.setUserId(42L);
        unpaid.setAmount(500f);
        unpaid.setPaymentStatus(PaymentStatus.UNPAID.name());
    }

    @Test
    void pay_validReceipt_marksAsPaidAndPublishesEvent() {
        when(receiptRepository.findById(1)).thenReturn(Optional.of(unpaid));
        when(receiptRepository.save(any(Receipt.class))).thenAnswer(inv -> inv.getArgument(0));

        Receipt result = receiptService.pay(42L, 1);

        assertEquals(PaymentStatus.PAID.name(), result.getPaymentStatus());
        assertNotNull(result.getDataTranzactie());
        verify(auditLogService).log(eq(42L), eq("PAY"), eq("Receipt"), eq("1"), any());
        verify(eventPublisher).publish(any(), any());
    }

    @Test
    void pay_alreadyPaid_throwsBadRequest() {
        unpaid.setPaymentStatus(PaymentStatus.PAID.name());
        when(receiptRepository.findById(1)).thenReturn(Optional.of(unpaid));

        assertThrows(BadRequestException.class, () -> receiptService.pay(42L, 1));
        verify(receiptRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void pay_wrongUser_throwsBadRequest() {
        when(receiptRepository.findById(1)).thenReturn(Optional.of(unpaid));

        assertThrows(BadRequestException.class, () -> receiptService.pay(99L, 1));
        verify(receiptRepository, never()).save(any());
    }

    @Test
    void findById_missing_throwsResourceNotFound() {
        when(receiptRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> receiptService.findById(99));
    }
}

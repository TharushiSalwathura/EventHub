package com.eventhub.payment.service;

import com.eventhub.payment.dto.PaymentResponse;
import com.eventhub.payment.dto.ProcessPaymentRequest;
import com.eventhub.payment.exception.ResourceNotFoundException;
import com.eventhub.payment.model.Payment;
import com.eventhub.payment.model.enums.PaymentMethod;
import com.eventhub.payment.model.enums.PaymentStatus;
import com.eventhub.payment.repository.PaymentRepository;
import com.eventhub.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "bookingServiceUrl", "http://localhost:8083");
        ReflectionTestUtils.setField(paymentService, "bookingServiceKey", "test-key");

        samplePayment = Payment.builder()
                .id(1L)
                .bookingId(101L)
                .userId(1L)
                .amount(5000.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.SUCCESS)
                .transactionReference("TXN-12345678")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void processPayment_Success() {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .bookingId(101L)
                .userId(1L)
                .amount(5000.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals(101L, response.getBookingId());
        assertEquals(5000.0, response.getAmount());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertNotNull(response.getTransactionReference());
        assertTrue(response.getTransactionReference().startsWith("TXN-"));

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(notificationService, times(1)).sendNotification(any());
    }

    @Test
    void getPaymentById_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(samplePayment));

        PaymentResponse response = paymentService.getPaymentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("TXN-12345678", response.getTransactionReference());
    }

    @Test
    void getPaymentById_NotFound() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentById(999L));
    }

    @Test
    void getPaymentByBookingId_Success() {
        when(paymentRepository.findByBookingId(101L)).thenReturn(Optional.of(samplePayment));

        PaymentResponse response = paymentService.getPaymentByBookingId(101L);

        assertNotNull(response);
        assertEquals(101L, response.getBookingId());
    }

    @Test
    void getAllPayments_Success() {
        when(paymentRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(samplePayment));

        List<PaymentResponse> list = paymentService.getAllPayments();

        assertNotNull(list);
        assertEquals(1, list.size());
    }
}

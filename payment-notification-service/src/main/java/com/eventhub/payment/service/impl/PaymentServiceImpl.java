package com.eventhub.payment.service.impl;

import com.eventhub.payment.dto.PaymentResponse;
import com.eventhub.payment.dto.ProcessPaymentRequest;
import com.eventhub.payment.dto.SendNotificationRequest;
import com.eventhub.payment.exception.ResourceNotFoundException;
import com.eventhub.payment.model.Payment;
import com.eventhub.payment.model.enums.NotificationType;
import com.eventhub.payment.model.enums.PaymentMethod;
import com.eventhub.payment.model.enums.PaymentStatus;
import com.eventhub.payment.repository.PaymentRepository;
import com.eventhub.payment.service.NotificationService;
import com.eventhub.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Value("${services.booking.url:http://localhost:8083}")
    private String bookingServiceUrl;

    @Value("${services.booking.key:booking-service-secret-key-12345}")
    private String bookingServiceKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        log.info("Processing payment of Rs. {} for Booking ID: {} (Method: {})",
                request.getAmount(), request.getBookingId(), request.getPaymentMethod());

        PaymentMethod method = request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.CREDIT_CARD;
        Long userId = request.getUserId() != null ? request.getUserId() : 1L;

        String txnRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .userId(userId)
                .amount(request.getAmount())
                .paymentMethod(method)
                .status(PaymentStatus.SUCCESS)
                .transactionReference(txnRef)
                .createdAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed successfully with ID: {} and Ref: {}", savedPayment.getId(), txnRef);

        // 1. Dispatch notification alert to user
        try {
            notificationService.sendNotification(SendNotificationRequest.builder()
                    .userId(userId)
                    .message(String.format("Payment of Rs. %.2f Successful for Booking #%d. Transaction ID: %s",
                            request.getAmount(), request.getBookingId(), txnRef))
                    .type(NotificationType.EMAIL)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to dispatch payment confirmation notification: {}", e.getMessage());
        }

        // 2. Automatically confirm booking in Booking Service if available
        try {
            confirmBookingInBookingService(request.getBookingId());
        } catch (Exception e) {
            log.warn("Could not automatically notify booking service for booking ID {}: {}", request.getBookingId(), e.getMessage());
        }

        return mapToResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment receipt not found for ID: " + id));
        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment receipt not found for Booking ID: " + bookingId));
        return mapToResponse(payment);
    }

    private void confirmBookingInBookingService(Long bookingId) {
        String url = String.format("%s/bookings/%d/confirm", bookingServiceUrl, bookingId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-KEY", bookingServiceKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        log.info("Booking #{} successfully confirmed via Booking Service API", bookingId);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        String formattedTimestamp = payment.getCreatedAt() != null
                ? payment.getCreatedAt().format(FORMATTER)
                : LocalDateTime.now().format(FORMATTER);

        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionReference(payment.getTransactionReference())
                .transactionId(payment.getTransactionReference())
                .createdAt(payment.getCreatedAt())
                .timestamp(formattedTimestamp)
                .build();
    }
}

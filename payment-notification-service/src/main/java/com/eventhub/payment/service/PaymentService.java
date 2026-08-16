package com.eventhub.payment.service;

import com.eventhub.payment.dto.PaymentResponse;
import com.eventhub.payment.dto.ProcessPaymentRequest;

import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(ProcessPaymentRequest request);

    List<PaymentResponse> getAllPayments();

    PaymentResponse getPaymentById(Long id);

    PaymentResponse getPaymentByBookingId(Long bookingId);
}

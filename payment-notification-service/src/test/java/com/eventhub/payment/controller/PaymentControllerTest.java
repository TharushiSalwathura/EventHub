package com.eventhub.payment.controller;

import com.eventhub.payment.dto.PaymentResponse;
import com.eventhub.payment.dto.ProcessPaymentRequest;
import com.eventhub.payment.model.enums.PaymentMethod;
import com.eventhub.payment.model.enums.PaymentStatus;
import com.eventhub.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private static final String API_KEY = "payment-service-secret-key-12345";

    @Test
    void processPayment_WithoutApiKey_ReturnsUnauthorized() throws Exception {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .bookingId(101L)
                .amount(5000.0)
                .build();

        mockMvc.perform(post("/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void processPayment_WithValidApiKey_ReturnsSuccess() throws Exception {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .bookingId(101L)
                .userId(1L)
                .amount(5000.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .bookingId(101L)
                .userId(1L)
                .amount(5000.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.SUCCESS)
                .transactionReference("TXN-12345678")
                .transactionId("TXN-12345678")
                .createdAt(LocalDateTime.now())
                .timestamp("2026-08-16 21:50:00")
                .build();

        when(paymentService.processPayment(any(ProcessPaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/payments/process")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionReference").value("TXN-12345678"))
                .andExpect(jsonPath("$.transactionId").value("TXN-12345678"));
    }

    @Test
    void getAllPayments_WithValidApiKey_ReturnsList() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .bookingId(101L)
                .amount(5000.0)
                .status(PaymentStatus.SUCCESS)
                .transactionReference("TXN-12345678")
                .build();

        when(paymentService.getAllPayments()).thenReturn(List.of(response));

        mockMvc.perform(get("/payments")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getPaymentById_WithValidApiKey_ReturnsPayment() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .bookingId(101L)
                .amount(5000.0)
                .status(PaymentStatus.SUCCESS)
                .transactionReference("TXN-12345678")
                .build();

        when(paymentService.getPaymentById(1L)).thenReturn(response);

        mockMvc.perform(get("/payments/1")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getPaymentByBookingId_WithValidApiKey_ReturnsPayment() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .bookingId(101L)
                .amount(5000.0)
                .status(PaymentStatus.SUCCESS)
                .transactionReference("TXN-12345678")
                .build();

        when(paymentService.getPaymentByBookingId(101L)).thenReturn(response);

        mockMvc.perform(get("/payments/booking/101")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(101L));
    }
}

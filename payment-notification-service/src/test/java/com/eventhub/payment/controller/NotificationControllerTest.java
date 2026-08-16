package com.eventhub.payment.controller;

import com.eventhub.payment.dto.NotificationResponse;
import com.eventhub.payment.dto.SendNotificationRequest;
import com.eventhub.payment.model.enums.NotificationStatus;
import com.eventhub.payment.model.enums.NotificationType;
import com.eventhub.payment.service.NotificationService;
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
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    private static final String API_KEY = "payment-service-secret-key-12345";

    @Test
    void sendNotification_WithoutApiKey_ReturnsUnauthorized() throws Exception {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(1L)
                .message("Test notification")
                .type(NotificationType.EMAIL)
                .build();

        mockMvc.perform(post("/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sendNotification_WithValidApiKey_ReturnsCreated() throws Exception {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(1L)
                .message("Test notification")
                .type(NotificationType.EMAIL)
                .build();

        NotificationResponse response = NotificationResponse.builder()
                .id(1L)
                .userId(1L)
                .message("Test notification")
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.SENT)
                .createdAt(LocalDateTime.now())
                .timestamp("2026-08-16 21:50:00")
                .build();

        when(notificationService.sendNotification(any(SendNotificationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/notifications/send")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.message").value("Test notification"))
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void getAllNotifications_WithValidApiKey_ReturnsList() throws Exception {
        NotificationResponse response = NotificationResponse.builder()
                .id(1L)
                .userId(1L)
                .message("Test notification")
                .status(NotificationStatus.SENT)
                .build();

        when(notificationService.getAllNotifications()).thenReturn(List.of(response));

        mockMvc.perform(get("/notifications")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getNotificationsByUserId_WithValidApiKey_ReturnsList() throws Exception {
        NotificationResponse response = NotificationResponse.builder()
                .id(1L)
                .userId(1L)
                .message("Test notification")
                .status(NotificationStatus.SENT)
                .build();

        when(notificationService.getNotificationsByUserId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/notifications/user/1")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}

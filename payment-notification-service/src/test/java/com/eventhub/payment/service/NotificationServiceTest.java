package com.eventhub.payment.service;

import com.eventhub.payment.dto.NotificationResponse;
import com.eventhub.payment.dto.SendNotificationRequest;
import com.eventhub.payment.model.Notification;
import com.eventhub.payment.model.enums.NotificationStatus;
import com.eventhub.payment.model.enums.NotificationType;
import com.eventhub.payment.repository.NotificationRepository;
import com.eventhub.payment.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification sampleNotification;

    @BeforeEach
    void setUp() {
        sampleNotification = Notification.builder()
                .id(1L)
                .userId(1L)
                .message("Payment Successful for Booking #101")
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void sendNotification_Success() {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(1L)
                .message("Payment Successful for Booking #101")
                .type(NotificationType.EMAIL)
                .build();

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(1L);
            return n;
        });

        NotificationResponse response = notificationService.sendNotification(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Payment Successful for Booking #101", response.getMessage());
        assertEquals(NotificationStatus.SENT, response.getStatus());

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getAllNotifications_Success() {
        when(notificationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(sampleNotification));

        List<NotificationResponse> list = notificationService.getAllNotifications();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(sampleNotification.getMessage(), list.get(0).getMessage());
    }

    @Test
    void getNotificationsByUserId_Success() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(sampleNotification));

        List<NotificationResponse> list = notificationService.getNotificationsByUserId(1L);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).getUserId());
    }
}

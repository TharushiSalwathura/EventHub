package com.eventhub.payment.service.impl;

import com.eventhub.payment.dto.NotificationResponse;
import com.eventhub.payment.dto.SendNotificationRequest;
import com.eventhub.payment.model.Notification;
import com.eventhub.payment.model.enums.NotificationStatus;
import com.eventhub.payment.model.enums.NotificationType;
import com.eventhub.payment.repository.NotificationRepository;
import com.eventhub.payment.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        log.info("Dispatching notification via [{}] to user ID: {}", request.getType(), request.getUserId());

        NotificationType type = request.getType() != null ? request.getType() : NotificationType.EMAIL;
        Long nextId = Math.abs(new Random().nextLong() % 900000L) + 100000L;

        Notification notification = Notification.builder()
                .id(nextId)
                .userId(request.getUserId())
                .message(request.getMessage())
                .type(type)
                .status(NotificationStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notification successfully logged and sent with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse mapToResponse(Notification notification) {
        String formattedTimestamp = notification.getCreatedAt() != null
                ? notification.getCreatedAt().format(FORMATTER)
                : LocalDateTime.now().format(FORMATTER);

        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .message(notification.getMessage())
                .type(notification.getType())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .timestamp(formattedTimestamp)
                .build();
    }
}

package com.eventhub.payment.service;

import com.eventhub.payment.dto.NotificationResponse;
import com.eventhub.payment.dto.SendNotificationRequest;

import java.util.List;

public interface NotificationService {

    NotificationResponse sendNotification(SendNotificationRequest request);

    List<NotificationResponse> getAllNotifications();

    List<NotificationResponse> getNotificationsByUserId(Long userId);
}

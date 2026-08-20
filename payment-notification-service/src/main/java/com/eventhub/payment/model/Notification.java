package com.eventhub.payment.model;

import com.eventhub.payment.model.enums.NotificationStatus;
import com.eventhub.payment.model.enums.NotificationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private Long id;

    private Long userId;

    private String message;

    private NotificationType type;

    private NotificationStatus status;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

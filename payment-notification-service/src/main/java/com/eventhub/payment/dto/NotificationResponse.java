package com.eventhub.payment.dto;

import com.eventhub.payment.model.enums.NotificationStatus;
import com.eventhub.payment.model.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Notification audit and delivery record")
public class NotificationResponse {

    @Schema(description = "Notification record ID", example = "1")
    private Long id;

    @Schema(description = "Recipient user ID", example = "1")
    private Long userId;

    @Schema(description = "Message body", example = "Payment Successful for Booking #101")
    private String message;

    @Schema(description = "Channel type", example = "EMAIL")
    private NotificationType type;

    @Schema(description = "Delivery status", example = "SENT")
    private NotificationStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Creation timestamp", example = "2026-08-16 21:50:00")
    private LocalDateTime createdAt;

    @Schema(description = "Formatted timestamp string for UI display", example = "2026-08-16 21:50:00")
    private String timestamp;
}

package com.eventhub.payment.dto;

import com.eventhub.payment.model.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body to dispatch a notification or audit alert")
public class SendNotificationRequest {

    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the recipient user", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotBlank(message = "Message content is required")
    @Schema(description = "Notification message text", example = "Your booking #101 has been confirmed.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "Notification dispatch channel", example = "EMAIL", defaultValue = "EMAIL")
    private NotificationType type;
}

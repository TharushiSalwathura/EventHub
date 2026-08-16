package com.eventhub.payment.dto;

import com.eventhub.payment.model.enums.PaymentMethod;
import com.eventhub.payment.model.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payment transaction receipt details")
public class PaymentResponse {

    @Schema(description = "Unique payment ID", example = "1")
    private Long id;

    @Schema(description = "Associated booking ID", example = "101")
    private Long bookingId;

    @Schema(description = "User ID who made the payment", example = "1")
    private Long userId;

    @Schema(description = "Amount charged", example = "5000.00")
    private Double amount;

    @Schema(description = "Payment method utilized", example = "CREDIT_CARD")
    private PaymentMethod paymentMethod;

    @Schema(description = "Status of the payment", example = "SUCCESS")
    private PaymentStatus status;

    @Schema(description = "Unique transaction reference code", example = "TXN-84920482")
    private String transactionReference;

    @Schema(description = "Transaction reference alias for frontend UI", example = "TXN-84920482")
    private String transactionId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Payment timestamp", example = "2026-08-16 21:50:00")
    private LocalDateTime createdAt;

    @Schema(description = "Formatted timestamp string for UI display", example = "2026-08-16 21:50:00")
    private String timestamp;
}

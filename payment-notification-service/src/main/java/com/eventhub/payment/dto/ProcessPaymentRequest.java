package com.eventhub.payment.dto;

import com.eventhub.payment.model.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body to process a payment for an event booking")
public class ProcessPaymentRequest {

    @NotNull(message = "Booking ID is required")
    @Schema(description = "ID of the booking being paid for", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookingId;

    @Schema(description = "ID of the user making the payment", example = "1")
    private Long userId;

    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be greater than zero")
    @Schema(description = "Amount to be charged", example = "5000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double amount;

    @Schema(description = "Method of payment", example = "CREDIT_CARD", defaultValue = "CREDIT_CARD")
    private PaymentMethod paymentMethod;

    @Schema(description = "Mock Card number for simulation", example = "4532XXXXXXXX1234")
    private String cardNumber;

    @Schema(description = "Mock CVV for simulation", example = "123")
    private String cvv;

    @Schema(description = "Mock card expiration date", example = "12/28")
    private String expiryDate;
}

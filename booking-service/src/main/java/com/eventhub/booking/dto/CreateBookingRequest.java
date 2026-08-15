package com.eventhub.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new booking")
public class CreateBookingRequest {

    @NotNull(message = "Event ID is required")
    @Schema(description = "ID of the event to book", example = "1")
    private Long eventId;

    @Schema(description = "Title of the event", example = "Tech Conference 2026")
    private String eventTitle;

    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user making the booking", example = "1")
    private Long userId;

    @NotNull(message = "Number of tickets is required")
    @Min(value = 1, message = "At least 1 ticket must be booked")
    @Schema(description = "Number of tickets requested", example = "2")
    private Integer tickets;

    @Schema(description = "Price per ticket (optional if totalAmount is given)", example = "2500.0")
    private Double unitPrice;

    @Schema(description = "Total amount for the booking (optional, calculated automatically if omitted)", example = "5000.0")
    private Double totalAmount;
}

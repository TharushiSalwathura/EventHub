package com.eventhub.booking.dto;

import com.eventhub.booking.model.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating an existing booking")
public class UpdateBookingRequest {

    @Min(value = 1, message = "At least 1 ticket must be specified")
    @Schema(description = "Updated ticket count", example = "3")
    private Integer tickets;

    @Schema(description = "Updated booking status", example = "CONFIRMED")
    private BookingStatus status;

    @Schema(description = "Updated total amount", example = "7500.0")
    private Double totalAmount;
}

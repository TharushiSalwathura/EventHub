package com.eventhub.booking.dto;

import com.eventhub.booking.model.BookingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard booking response payload")
public class BookingResponse {

    @Schema(description = "Unique booking identifier", example = "101")
    private Long id;

    @Schema(description = "ID of the booked event", example = "1")
    private Long eventId;

    @Schema(description = "Title of the booked event", example = "Tech Conference 2026")
    private String eventTitle;

    @Schema(description = "ID of the user who booked", example = "1")
    private Long userId;

    @Schema(description = "Number of booked tickets", example = "2")
    private Integer tickets;

    @Schema(description = "Unit price per ticket", example = "2500.0")
    private Double unitPrice;

    @Schema(description = "Total cost for the booking", example = "5000.0")
    private Double totalAmount;

    @Schema(description = "Current lifecycle status of booking", example = "PENDING")
    private BookingStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp when booking was created", example = "2026-08-15T15:30:00")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp when booking was last updated", example = "2026-08-15T15:30:00")
    private LocalDateTime updatedAt;
}

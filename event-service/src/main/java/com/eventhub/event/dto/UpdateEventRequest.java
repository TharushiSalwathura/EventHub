package com.eventhub.event.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for updating an existing event (PUT /events/{id}).
 * All fields are optional; only non-null fields will be applied.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventRequest {

    private String title;

    private String description;

    private String location;

    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    @Positive(message = "Capacity must be a positive number")
    private Integer capacity;

    @PositiveOrZero(message = "Available seats cannot be negative")
    private Integer availableSeats;

    @Positive(message = "Price must be a positive value")
    private BigDecimal price;
}

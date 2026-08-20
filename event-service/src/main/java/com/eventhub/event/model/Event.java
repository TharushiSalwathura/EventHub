package com.eventhub.event.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MongoDB Document representing an Event in the EventHub system.
 * Maps to the "events" collection in the eventhub_event MongoDB database.
 */
@Document(collection = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    private Long id;

    @NotBlank(message = "Event title is required")
    private String title;

    private String description;

    @NotBlank(message = "Event location is required")
    private String location;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be a positive number")
    private Integer capacity;

    @NotNull(message = "Available seats is required")
    @PositiveOrZero(message = "Available seats cannot be negative")
    private Integer availableSeats;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive value")
    private BigDecimal price;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}

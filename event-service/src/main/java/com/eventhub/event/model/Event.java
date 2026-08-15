package com.eventhub.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity representing an Event in the EventHub system.
 * Maps to the "events" table in the eventhub_event PostgreSQL database.
 */
@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human-readable event title. Required. */
    @NotBlank(message = "Event title is required")
    @Column(nullable = false, length = 255)
    private String title;

    /** Optional full description / agenda of the event. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Physical or virtual location of the event. Required. */
    @NotBlank(message = "Event location is required")
    @Column(nullable = false, length = 500)
    private String location;

    /** Date and time when the event takes place. Required. */
    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    /** Maximum number of attendees allowed. Must be positive. */
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be a positive number")
    @Column(nullable = false)
    private Integer capacity;

    /** Number of seats still available for booking. Cannot go below 0. */
    @NotNull(message = "Available seats is required")
    @PositiveOrZero(message = "Available seats cannot be negative")
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    /** Ticket price in local currency (e.g., LKR). Must be positive. */
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive value")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Auto-populated timestamp when the record is first created. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Auto-updated timestamp whenever the record is modified. */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

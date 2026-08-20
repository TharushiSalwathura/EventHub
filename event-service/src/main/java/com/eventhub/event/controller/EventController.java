package com.eventhub.event.controller;

import com.eventhub.event.dto.CreateEventRequest;
import com.eventhub.event.dto.EventResponse;
import com.eventhub.event.dto.ErrorResponse;
import com.eventhub.event.dto.UpdateEventRequest;
import com.eventhub.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller exposing all Event Management endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "APIs for creating, reading, updating, deleting, and searching events")
public class EventController {

    private final EventService eventService;

    @Operation(
        summary = "Create a new event",
        description = "Creates a new event. `availableSeats` is automatically set to the provided `capacity`. Requires a valid X-API-KEY header.",
        security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Event created successfully",
            content = @Content(schema = @Schema(implementation = EventResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request) {

        log.info("POST /events - Creating event: {}", request.getTitle());
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "List all events",
        description = "Returns all events stored in the system. Public endpoint — no API key required."
    )
    @ApiResponse(responseCode = "200", description = "List of events retrieved",
        content = @Content(schema = @Schema(implementation = EventResponse.class)))
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        log.info("GET /events - Fetching all events");
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @Operation(
        summary = "Search events by title or location",
        description = "Case-insensitive search across event titles and locations. Example: `?query=Colombo`"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search results returned",
            content = @Content(schema = @Schema(implementation = EventResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<EventResponse>> searchEvents(
            @Parameter(description = "Search term to match against event title or location", required = false, example = "Colombo")
            @RequestParam(name = "query", required = false, defaultValue = "") String query) {

        log.info("GET /events/search - query='{}'", query);
        return ResponseEntity.ok(eventService.searchEvents(query));
    }

    @Operation(
        summary = "Get event details by ID",
        description = "Returns full details of a single event. Public endpoint — no API key required."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Event found",
            content = @Content(schema = @Schema(implementation = EventResponse.class))),
        @ApiResponse(responseCode = "404", description = "Event not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @Parameter(description = "The event's unique identifier", required = true, example = "1")
            @PathVariable Long id) {

        log.info("GET /events/{}", id);
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @Operation(
        summary = "Check available seats for an event",
        description = "Returns capacity, available seats, and booked seats for the specified event.",
        security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Availability information returned"),
        @ApiResponse(responseCode = "404", description = "Event not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @Parameter(description = "The event's unique identifier", required = true, example = "1")
            @PathVariable Long id) {

        log.info("GET /events/{}/availability", id);
        return ResponseEntity.ok(eventService.checkAvailability(id));
    }

    @Operation(
        summary = "Update event details",
        description = "Partially updates an event. Only provided (non-null) fields are modified. Requires a valid X-API-KEY header.",
        security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Event updated successfully",
            content = @Content(schema = @Schema(implementation = EventResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Event not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @Parameter(description = "The event's unique identifier", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request) {

        log.info("PUT /events/{}", id);
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @Operation(
        summary = "Delete an event",
        description = "Permanently removes an event from the system. Requires a valid X-API-KEY header.",
        security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-KEY",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Event not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "The event's unique identifier", required = true, example = "1")
            @PathVariable Long id) {

        log.info("DELETE /events/{}", id);
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Reduce available seats (internal)",
        description = "Called by the booking-service to reserve seats after a booking is confirmed. Requires X-API-KEY.",
        security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Seats reduced successfully",
            content = @Content(schema = @Schema(implementation = EventResponse.class))),
        @ApiResponse(responseCode = "409", description = "Not enough seats available",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Event not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/reduce-seats")
    public ResponseEntity<EventResponse> reduceSeats(
            @PathVariable Long id,
            @Parameter(description = "Number of seats to reserve", required = true, example = "2")
            @RequestParam int count) {

        log.info("POST /events/{}/reduce-seats?count={}", id, count);
        return ResponseEntity.ok(eventService.reduceSeats(id, count));
    }

    @Operation(
        summary = "Release seats back to pool (internal)",
        description = "Called by the booking-service to return seats when a booking is cancelled. Requires X-API-KEY.",
        security = @SecurityRequirement(name = "ApiKeyAuth")
    )
    @ApiResponse(responseCode = "200", description = "Seats released successfully",
        content = @Content(schema = @Schema(implementation = EventResponse.class)))
    @PostMapping("/{id}/release-seats")
    public ResponseEntity<EventResponse> releaseSeats(
            @PathVariable Long id,
            @Parameter(description = "Number of seats to release", required = true, example = "2")
            @RequestParam int count) {

        log.info("POST /events/{}/release-seats?count={}", id, count);
        return ResponseEntity.ok(eventService.releaseSeats(id, count));
    }
}

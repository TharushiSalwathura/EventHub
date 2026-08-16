package com.eventhub.booking.controller;

import com.eventhub.booking.dto.BookingResponse;
import com.eventhub.booking.dto.CreateBookingRequest;
import com.eventhub.booking.dto.ErrorResponse;
import com.eventhub.booking.dto.UpdateBookingRequest;
import com.eventhub.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Booking Management", description = "Endpoints for managing event ticket bookings, cancellations, and status lifecycles")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Create a new booking", description = "Creates a new event booking with an initial status of PENDING")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid booking request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        log.info("Received booking request from user email: {} (role: {}) for event: {}",
                userEmail, userRole, request.getEventId());
        BookingResponse createdBooking = bookingService.createBooking(request);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @Operation(summary = "List all bookings", description = "Retrieves a list of all event bookings across the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of bookings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get booking by ID", description = "Retrieves complete booking details for a given booking ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking found and retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @Parameter(description = "ID of the booking to retrieve", required = true, example = "1")
            @PathVariable Long id
    ) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @Operation(summary = "Get bookings by User ID", description = "Retrieves all bookings made by a specific user, ordered from newest to oldest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User bookings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserId(
            @Parameter(description = "ID of the user whose bookings are requested", required = true, example = "1")
            @PathVariable Long userId
    ) {
        List<BookingResponse> userBookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(userBookings);
    }

    @Operation(summary = "Get bookings by Event ID", description = "Retrieves all bookings made for a specific event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event bookings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByEventId(
            @Parameter(description = "ID of the event whose bookings are requested", required = true, example = "1")
            @PathVariable Long eventId
    ) {
        List<BookingResponse> eventBookings = bookingService.getBookingsByEventId(eventId);
        return ResponseEntity.ok(eventBookings);
    }

    @Operation(summary = "Update booking details", description = "Updates ticket counts, total amount, or status for an existing booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking updated successfully",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update payload or cancelled booking",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @Parameter(description = "ID of the booking to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingRequest request
    ) {
        BookingResponse updatedBooking = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(updatedBooking);
    }

    @Operation(summary = "Cancel a booking", description = "Transitions the booking status to CANCELLED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking cancelled successfully",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Booking already cancelled",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @Parameter(description = "ID of the booking to cancel", required = true, example = "1")
            @PathVariable Long id
    ) {
        BookingResponse cancelledBooking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(cancelledBooking);
    }

    @Operation(summary = "Confirm a booking", description = "Transitions the booking status to CONFIRMED (typically after successful payment)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking confirmed successfully",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Cannot confirm cancelled booking",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @Parameter(description = "ID of the booking to confirm", required = true, example = "1")
            @PathVariable Long id
    ) {
        BookingResponse confirmedBooking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(confirmedBooking);
    }

    @Operation(summary = "Delete a booking", description = "Permanently deletes a booking record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(
            @Parameter(description = "ID of the booking to delete", required = true, example = "1")
            @PathVariable Long id
    ) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}

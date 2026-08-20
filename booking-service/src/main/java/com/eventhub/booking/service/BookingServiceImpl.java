package com.eventhub.booking.service;

import com.eventhub.booking.dto.BookingResponse;
import com.eventhub.booking.dto.CreateBookingRequest;
import com.eventhub.booking.dto.UpdateBookingRequest;
import com.eventhub.booking.exception.InvalidBookingException;
import com.eventhub.booking.exception.ResourceNotFoundException;
import com.eventhub.booking.model.Booking;
import com.eventhub.booking.model.BookingStatus;
import com.eventhub.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
        log.info("Creating booking for eventId: {}, userId: {}, tickets: {}",
                request.getEventId(), request.getUserId(), request.getTickets());

        if (request.getTickets() == null || request.getTickets() < 1) {
            throw new InvalidBookingException("Ticket count must be at least 1");
        }

        Double totalAmount = request.getTotalAmount();
        if (totalAmount == null || totalAmount <= 0) {
            if (request.getUnitPrice() != null && request.getUnitPrice() >= 0) {
                totalAmount = request.getTickets() * request.getUnitPrice();
            } else {
                totalAmount = 0.0;
            }
        }

        Long nextId = Math.abs(new Random().nextLong() % 900000L) + 100000L;

        Booking booking = Booking.builder()
                .id(nextId)
                .eventId(request.getEventId())
                .eventTitle(request.getEventTitle())
                .userId(request.getUserId())
                .tickets(request.getTickets())
                .unitPrice(request.getUnitPrice())
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Successfully created booking with id: {}", savedBooking.getId());
        return mapToResponse(savedBooking);
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse getBookingById(Long id) {
        log.info("Fetching booking by id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));
        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        log.info("Fetching bookings for userId: {}", userId);
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getBookingsByEventId(Long eventId) {
        log.info("Fetching bookings for eventId: {}", eventId);
        return bookingRepository.findByEventId(eventId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse updateBooking(Long id, UpdateBookingRequest request) {
        log.info("Updating booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("Cannot update a cancelled booking");
        }

        if (request.getTickets() != null) {
            if (request.getTickets() < 1) {
                throw new InvalidBookingException("Ticket count must be at least 1");
            }
            booking.setTickets(request.getTickets());
            if (booking.getUnitPrice() != null) {
                booking.setTotalAmount(request.getTickets() * booking.getUnitPrice());
            }
        }

        if (request.getTotalAmount() != null && request.getTotalAmount() >= 0) {
            booking.setTotalAmount(request.getTotalAmount());
        }

        if (request.getStatus() != null) {
            booking.setStatus(request.getStatus());
        }

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Successfully updated booking id: {}", updatedBooking.getId());
        return mapToResponse(updatedBooking);
    }

    @Override
    public BookingResponse cancelBooking(Long id) {
        log.info("Cancelling booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("Booking with ID " + id + " is already CANCELLED");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Successfully cancelled booking id: {}", id);
        return mapToResponse(updatedBooking);
    }

    @Override
    public BookingResponse confirmBooking(Long id) {
        log.info("Confirming booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("Cannot confirm a cancelled booking with ID " + id);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Successfully confirmed booking id: {}", id);
        return mapToResponse(updatedBooking);
    }

    @Override
    public void deleteBooking(Long id) {
        log.info("Deleting booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));
        bookingRepository.delete(booking);
        log.info("Successfully deleted booking id: {}", id);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .eventId(booking.getEventId())
                .eventTitle(booking.getEventTitle())
                .userId(booking.getUserId())
                .tickets(booking.getTickets())
                .unitPrice(booking.getUnitPrice())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}

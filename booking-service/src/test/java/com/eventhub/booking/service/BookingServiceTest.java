package com.eventhub.booking.service;

import com.eventhub.booking.dto.BookingResponse;
import com.eventhub.booking.dto.CreateBookingRequest;
import com.eventhub.booking.dto.UpdateBookingRequest;
import com.eventhub.booking.exception.InvalidBookingException;
import com.eventhub.booking.exception.ResourceNotFoundException;
import com.eventhub.booking.model.Booking;
import com.eventhub.booking.model.BookingStatus;
import com.eventhub.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking mockBooking;

    @BeforeEach
    void setUp() {
        mockBooking = Booking.builder()
                .id(1L)
                .eventId(10L)
                .eventTitle("Tech Conference 2026")
                .userId(5L)
                .tickets(2)
                .unitPrice(2500.0)
                .totalAmount(5000.0)
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateBooking_Success() {
        CreateBookingRequest request = CreateBookingRequest.builder()
                .eventId(10L)
                .eventTitle("Tech Conference 2026")
                .userId(5L)
                .tickets(2)
                .unitPrice(2500.0)
                .build();

        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(BookingStatus.PENDING, response.getStatus());
        assertEquals(5000.0, response.getTotalAmount());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_InvalidTickets() {
        CreateBookingRequest request = CreateBookingRequest.builder()
                .eventId(10L)
                .userId(5L)
                .tickets(0)
                .build();

        assertThrows(InvalidBookingException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void testGetBookingById_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        BookingResponse response = bookingService.getBookingById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Tech Conference 2026", response.getEventTitle());
    }

    @Test
    void testGetBookingById_NotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(99L));
    }

    @Test
    void testGetBookingsByUserId() {
        when(bookingRepository.findByUserIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(mockBooking));

        List<BookingResponse> responses = bookingService.getBookingsByUserId(5L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(5L, responses.get(0).getUserId());
    }

    @Test
    void testCancelBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService.cancelBooking(1L);

        assertNotNull(response);
        assertEquals(BookingStatus.CANCELLED, response.getStatus());
    }

    @Test
    void testCancelBooking_AlreadyCancelled() {
        mockBooking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        assertThrows(InvalidBookingException.class, () -> bookingService.cancelBooking(1L));
    }

    @Test
    void testConfirmBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService.confirmBooking(1L);

        assertNotNull(response);
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
    }

    @Test
    void testConfirmBooking_CancelledBooking_Fails() {
        mockBooking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        assertThrows(InvalidBookingException.class, () -> bookingService.confirmBooking(1L));
    }

    @Test
    void testUpdateBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateBookingRequest updateRequest = UpdateBookingRequest.builder()
                .tickets(4)
                .build();

        BookingResponse response = bookingService.updateBooking(1L, updateRequest);

        assertNotNull(response);
        assertEquals(4, response.getTickets());
        assertEquals(10000.0, response.getTotalAmount());
    }
}

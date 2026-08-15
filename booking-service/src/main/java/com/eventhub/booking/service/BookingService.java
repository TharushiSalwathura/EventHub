package com.eventhub.booking.service;

import com.eventhub.booking.dto.BookingResponse;
import com.eventhub.booking.dto.CreateBookingRequest;
import com.eventhub.booking.dto.UpdateBookingRequest;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(CreateBookingRequest request);

    List<BookingResponse> getAllBookings();

    BookingResponse getBookingById(Long id);

    List<BookingResponse> getBookingsByUserId(Long userId);

    List<BookingResponse> getBookingsByEventId(Long eventId);

    BookingResponse updateBooking(Long id, UpdateBookingRequest request);

    BookingResponse cancelBooking(Long id);

    BookingResponse confirmBooking(Long id);

    void deleteBooking(Long id);
}

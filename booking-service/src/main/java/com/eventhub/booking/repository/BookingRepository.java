package com.eventhub.booking.repository;

import com.eventhub.booking.model.Booking;
import com.eventhub.booking.model.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByEventId(Long eventId);

    List<Booking> findByStatus(BookingStatus status);
}

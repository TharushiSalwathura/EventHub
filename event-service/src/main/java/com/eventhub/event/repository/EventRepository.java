package com.eventhub.event.repository;

import com.eventhub.event.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data MongoDB repository for the Event document.
 */
@Repository
public interface EventRepository extends MongoRepository<Event, Long> {

    List<Event> findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String title, String location);

    List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDateTime dateTime);

    List<Event> findByAvailableSeatsGreaterThan(Integer minSeats);
}

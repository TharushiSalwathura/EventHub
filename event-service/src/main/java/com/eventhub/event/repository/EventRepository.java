package com.eventhub.event.repository;

import com.eventhub.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the Event entity.
 * Provides CRUD + custom search / availability query methods.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Full-text case-insensitive search across title and location fields.
     *
     * @param title    substring to search in the title column
     * @param location substring to search in the location column
     * @return list of matching events (may be empty, never null)
     */
    List<Event> findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String title, String location);

    /**
     * Returns all events whose eventDate is after the given threshold.
     * Useful for listing only upcoming events.
     *
     * @param dateTime the cut-off datetime
     * @return list of future events ordered by their event date
     */
    @Query("SELECT e FROM Event e WHERE e.eventDate > :dateTime ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Finds all events that still have at least one available seat.
     *
     * @return list of events with availableSeats > 0
     */
    List<Event> findByAvailableSeatsGreaterThan(Integer minSeats);
}

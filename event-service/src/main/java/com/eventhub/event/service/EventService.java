package com.eventhub.event.service;

import com.eventhub.event.dto.CreateEventRequest;
import com.eventhub.event.dto.EventResponse;
import com.eventhub.event.dto.UpdateEventRequest;
import com.eventhub.event.exception.EventNotFoundException;
import com.eventhub.event.exception.InsufficientSeatsException;
import com.eventhub.event.model.Event;
import com.eventhub.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Business logic for the Event Management microservice.
 *
 * <ul>
 *   <li>CRUD operations on Events</li>
 *   <li>Seat capacity management (reduce / release seats atomically)</li>
 *   <li>Search by title or location</li>
 *   <li>Availability check endpoint</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    // ═══════════════════════════════════════════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Creates a new event.
     * availableSeats is automatically initialised to the full capacity.
     *
     * @param request validated creation payload
     * @return the persisted event as a response DTO
     */
    public EventResponse createEvent(CreateEventRequest request) {
        log.info("Creating new event: {}", request.getTitle());

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .eventDate(request.getEventDate())
                .capacity(request.getCapacity())
                .availableSeats(request.getCapacity()) // seats = full capacity at creation
                .price(request.getPrice())
                .build();

        Event saved = eventRepository.save(event);
        log.info("Event created with id={}", saved.getId());
        return toResponse(saved);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // READ
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Returns all events.
     *
     * @return list of all event response DTOs (may be empty)
     */
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single event by its primary key.
     *
     * @param id the event's database ID
     * @return the event response DTO
     * @throws EventNotFoundException if no event with that ID exists
     */
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        Event event = findEventOrThrow(id);
        return toResponse(event);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UPDATE
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Partially updates an event. Only non-null fields in the request are applied.
     * If capacity is updated and the new capacity is smaller than the number of
     * already-booked seats, an {@link IllegalArgumentException} is thrown.
     *
     * @param id      the event ID
     * @param request update payload (all fields optional)
     * @return the updated event response DTO
     */
    public EventResponse updateEvent(Long id, UpdateEventRequest request) {
        Event event = findEventOrThrow(id);
        log.info("Updating event id={}", id);

        if (request.getTitle() != null)       event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getLocation() != null)    event.setLocation(request.getLocation());
        if (request.getEventDate() != null)   event.setEventDate(request.getEventDate());
        if (request.getPrice() != null)       event.setPrice(request.getPrice());

        // Capacity adjustment: recalculate available seats proportionally
        if (request.getCapacity() != null) {
            int bookedSeats = event.getCapacity() - event.getAvailableSeats();
            if (request.getCapacity() < bookedSeats) {
                throw new IllegalArgumentException(
                        "New capacity (" + request.getCapacity() + ") is less than " +
                        "already-booked seats (" + bookedSeats + ")");
            }
            event.setCapacity(request.getCapacity());
            event.setAvailableSeats(request.getCapacity() - bookedSeats);
        }

        // Direct override of available seats (e.g., admin correction)
        if (request.getAvailableSeats() != null) {
            if (request.getAvailableSeats() > event.getCapacity()) {
                throw new IllegalArgumentException(
                        "Available seats cannot exceed total capacity (" + event.getCapacity() + ")");
            }
            event.setAvailableSeats(request.getAvailableSeats());
        }

        Event updated = eventRepository.save(event);
        log.info("Event id={} updated successfully", id);
        return toResponse(updated);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DELETE
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Deletes an event by ID.
     *
     * @param id the event ID
     * @throws EventNotFoundException if no event with that ID exists
     */
    public void deleteEvent(Long id) {
        Event event = findEventOrThrow(id);
        eventRepository.delete(event);
        log.info("Event id={} deleted", id);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SEARCH
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Case-insensitive full-text search across title and location.
     *
     * @param query the search term
     * @return matching events as response DTOs
     */
    @Transactional(readOnly = true)
    public List<EventResponse> searchEvents(String query) {
        log.debug("Searching events with query='{}'", query);
        return eventRepository
                .findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(query, query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // AVAILABILITY
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Returns availability information for an event.
     *
     * @param id the event ID
     * @return a map containing availability fields
     */
    @Transactional(readOnly = true)
    public Map<String, Object> checkAvailability(Long id) {
        Event event = findEventOrThrow(id);
        return Map.of(
                "eventId",        event.getId(),
                "title",          event.getTitle(),
                "capacity",       event.getCapacity(),
                "availableSeats", event.getAvailableSeats(),
                "bookedSeats",    event.getCapacity() - event.getAvailableSeats(),
                "available",      event.getAvailableSeats() > 0
        );
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SEAT MANAGEMENT (called by booking-service via internal API)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Atomically reduces the available seats by {@code count}.
     * Called by the booking-service when a booking is confirmed.
     *
     * @param id    event ID
     * @param count number of seats to reserve (must be >= 1)
     * @return updated event DTO
     * @throws InsufficientSeatsException if not enough seats are free
     */
    public EventResponse reduceSeats(Long id, int count) {
        Event event = findEventOrThrow(id);

        if (event.getAvailableSeats() < count) {
            throw new InsufficientSeatsException(id, count, event.getAvailableSeats());
        }

        event.setAvailableSeats(event.getAvailableSeats() - count);
        Event updated = eventRepository.save(event);
        log.info("Reduced {} seats for event id={}; remaining={}", count, id, updated.getAvailableSeats());
        return toResponse(updated);
    }

    /**
     * Releases (returns) seats back to the pool when a booking is cancelled.
     *
     * @param id    event ID
     * @param count number of seats to release
     * @return updated event DTO
     */
    public EventResponse releaseSeats(Long id, int count) {
        Event event = findEventOrThrow(id);
        int newSeats = Math.min(event.getAvailableSeats() + count, event.getCapacity());
        event.setAvailableSeats(newSeats);
        Event updated = eventRepository.save(event);
        log.info("Released {} seats for event id={}; now available={}", count, id, updated.getAvailableSeats());
        return toResponse(updated);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════════════════════

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    /**
     * Maps an {@link Event} entity to an {@link EventResponse} DTO.
     */
    private EventResponse toResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .eventDate(event.getEventDate())
                .capacity(event.getCapacity())
                .availableSeats(event.getAvailableSeats())
                .price(event.getPrice())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}

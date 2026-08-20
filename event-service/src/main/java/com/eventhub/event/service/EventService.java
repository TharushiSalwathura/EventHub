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

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Business logic for the Event Management microservice using MongoDB.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public EventResponse createEvent(CreateEventRequest request) {
        log.info("Creating new event: {}", request.getTitle());

        Long nextId = Math.abs(new Random().nextLong() % 900000L) + 100000L;

        Event event = Event.builder()
                .id(nextId)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .eventDate(request.getEventDate())
                .capacity(request.getCapacity())
                .availableSeats(request.getCapacity())
                .price(request.getPrice())
                .build();

        Event saved = eventRepository.save(event);
        log.info("Event created with id={}", saved.getId());
        return toResponse(saved);
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(Long id) {
        Event event = findEventOrThrow(id);
        return toResponse(event);
    }

    public EventResponse updateEvent(Long id, UpdateEventRequest request) {
        Event event = findEventOrThrow(id);
        log.info("Updating event id={}", id);

        if (request.getTitle() != null)       event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getLocation() != null)    event.setLocation(request.getLocation());
        if (request.getEventDate() != null)   event.setEventDate(request.getEventDate());
        if (request.getPrice() != null)       event.setPrice(request.getPrice());

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

    public void deleteEvent(Long id) {
        Event event = findEventOrThrow(id);
        eventRepository.delete(event);
        log.info("Event id={} deleted", id);
    }

    public List<EventResponse> searchEvents(String query) {
        log.debug("Searching events with query='{}'", query);
        return eventRepository
                .findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(query, query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

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

    public EventResponse releaseSeats(Long id, int count) {
        Event event = findEventOrThrow(id);
        int newSeats = Math.min(event.getAvailableSeats() + count, event.getCapacity());
        event.setAvailableSeats(newSeats);
        Event updated = eventRepository.save(event);
        log.info("Released {} seats for event id={}; now available={}", count, id, updated.getAvailableSeats());
        return toResponse(updated);
    }

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

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

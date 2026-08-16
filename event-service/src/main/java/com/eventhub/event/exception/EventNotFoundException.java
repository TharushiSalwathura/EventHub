package com.eventhub.event.exception;

/**
 * Thrown when an event with the requested ID does not exist in the database.
 */
public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(Long id) {
        super("Event not found with id: " + id);
    }
}

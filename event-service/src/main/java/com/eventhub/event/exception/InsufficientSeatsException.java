package com.eventhub.event.exception;

/**
 * Thrown when a booking or seat-reduction request cannot be satisfied
 * because the event has no remaining available seats.
 */
public class InsufficientSeatsException extends RuntimeException {

    public InsufficientSeatsException(Long eventId, int requested, int available) {
        super(String.format(
                "Insufficient seats for event %d: requested %d but only %d available",
                eventId, requested, available));
    }
}

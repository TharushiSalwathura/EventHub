package com.eventhub.event.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Standard error envelope returned for all 4xx / 5xx responses.
 * Consistent with the auth-service error format for uniform API behaviour.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}

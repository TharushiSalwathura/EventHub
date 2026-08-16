package com.eventhub.payment.controller;

import com.eventhub.payment.dto.ErrorResponse;
import com.eventhub.payment.dto.NotificationResponse;
import com.eventhub.payment.dto.SendNotificationRequest;
import com.eventhub.payment.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications & Audit Logs (Member 5)", description = "Endpoints for dispatching user notifications, logging system alerts, and viewing notification history")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Send a notification", description = "Dispatches a new notification to a specific user via Email/SMS/Push channels and records the audit log")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification dispatched and logged successfully",
                    content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid notification request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request
    ) {
        log.info("Received request to send notification to user ID: {}", request.getUserId());
        NotificationResponse response = notificationService.sendNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "List all system notifications", description = "Retrieves all notification audit logs in the system, ordered newest to oldest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of notifications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        List<NotificationResponse> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "List notifications by User ID", description = "Retrieves all notifications sent to a specific user, ordered from newest to oldest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User notifications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(
            @Parameter(description = "ID of the user whose notifications to retrieve", required = true, example = "1")
            @PathVariable Long userId
    ) {
        List<NotificationResponse> userNotifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(userNotifications);
    }
}

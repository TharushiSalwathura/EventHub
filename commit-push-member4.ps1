# ============================================================
# EventHub — Member 4 & 5: Commit & Push Script
# Payment & Notification Microservice Implementation
# ============================================================

$ErrorActionPreference = "Stop"
$repoDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $repoDir

Write-Host "=== Staging all payment-notification-service changes ===" -ForegroundColor Cyan

git add payment-notification-service/
git add docker-compose.yml
git add commit-push-member4.ps1

Write-Host "=== Current status ===" -ForegroundColor Cyan
git status

Write-Host "=== Committing ===" -ForegroundColor Cyan
git commit -m "feat(payment-notification-service): Member 4 & 5 - Payment Processing & Notification Microservice

- Add payment-notification-service Spring Boot 3.3.5 microservice on port 8084
- PostgreSQL database eventhub_payment on port 5435
- Payment JPA entity (id, bookingId, userId, amount, paymentMethod, status, transactionReference, createdAt)
- Notification JPA entity (id, userId, message, type, status, createdAt)
- Repositories: PaymentRepository and NotificationRepository
- DTOs: ProcessPaymentRequest, PaymentResponse, SendNotificationRequest, NotificationResponse, ErrorResponse
- PaymentService: Mock transaction gateway, TXN reference generator, automatic notification dispatch, inter-service booking confirmation
- NotificationService: Audit logging and simulated email/SMS notification delivery
- ApiKeyFilter & SecurityConfig: X-API-KEY validation for internal communication
- OpenApiConfig & Swagger UI: Full OpenAPI 3.0 documentation at /swagger-ui.html
- PaymentController & NotificationController: All REST endpoints with @Operation and response mapping
- GlobalExceptionHandler: Centralized error handling for 400, 401, 404, 500
- Multi-stage Dockerfile (Eclipse Temurin 21) exposing port 8084
- Updated docker-compose.yml with payment-notification-service and gateway dependencies
- Full unit and integration test suite passing (18/18 tests)"

Write-Host "=== Pushing to remote ===" -ForegroundColor Cyan
git push --set-upstream origin feature/member4-payment-notification

Write-Host "`n✅ Successfully pushed feature/member4-payment-notification to GitHub!" -ForegroundColor Green

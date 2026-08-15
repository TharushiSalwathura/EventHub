# ============================================================
# EventHub — Member 2: Commit & Push Script
# Run this AFTER the Maven build succeeds.
# ============================================================

$ErrorActionPreference = "Stop"
$repoDir = "C:\Users\Thilina Dilshan\Desktop\EventHub"
Set-Location $repoDir

Write-Host "=== Staging all event-service changes ===" -ForegroundColor Cyan

git add event-service/
git add docker-compose.yml

Write-Host "=== Current status ===" -ForegroundColor Cyan
git status

Write-Host "=== Committing ===" -ForegroundColor Cyan
git commit -m "feat(event-service): Member 2 - Complete Event Management Microservice

- Add event-service Spring Boot 3.3.5 microservice on port 8082
- PostgreSQL database eventhub_event on port 5433
- Event JPA entity with full field set (id, title, description, location,
  eventDate, capacity, availableSeats, price, createdAt, updatedAt)
- EventRepository with custom search by title/location
- DTOs: CreateEventRequest, UpdateEventRequest, EventResponse, ErrorResponse
- EventService: CRUD, search, availability check, seat reduce/release
- ApiKeyFilter: X-API-KEY validation with public GET bypass
- SecurityConfig: stateless security, Swagger public, API key enforcement
- OpenAPI 3.0 config: Swagger UI at /swagger-ui.html
- EventController: 9 endpoints with @Operation/@ApiResponse annotations
- GlobalExceptionHandler: 404, 409, 400, 500 coverage
- Two-stage Dockerfile (JDK builder + JRE runtime on port 8082)
- docker-compose.yml: added event-service + event-db dependency"

Write-Host "=== Pushing to remote ===" -ForegroundColor Cyan
git push --set-upstream origin feature/member2-event

Write-Host "`n✅ Successfully pushed feature/member2-event to GitHub!" -ForegroundColor Green

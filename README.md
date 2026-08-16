# EventHub — Microservices-Based Event Booking & Management System

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.3.5](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-2023.0.3-blue.svg)](https://spring.io/projects/spring-cloud-gateway)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-29.2.1-blue.svg)](https://www.docker.com/)

## 1. Project Overview

**EventHub** is a university group project designed using a **microservices architecture** where each student owns one distinct microservice / functional domain.

### Team Responsibilities & Task Distribution

| Member | Role | Assigned Domain / Subsystem | Port |
| :--- | :--- | :--- | :---: |
| **Member 1 (Group Leader)** | **Gateway & Auth Lead** | **User & Authentication Service + API Gateway + Frontend UI** | **8080 / 8081 / 3000** |
| Member 2 | Backend Developer | Event Management Service | 8082 |
| Member 3 | Backend Developer | Booking Service | 8083 |
| Member 4 | Backend Developer | Payment Processing & Transaction Management Service | 8084 |
| Member 5 | Backend Developer | Notification System, Event Dispatch & System Audit Logs | 8084 (Co-located) |

---

## 2. Architecture & Microservice Boundaries

```text
                    ┌─────────────────────┐
                    │    React Frontend   │
                    └──────────┬──────────┘
                               │
                               │ HTTP/HTTPS
                               ▼
                    ┌─────────────────────┐
                    │     API Gateway     │
                    │      (:8080)        │
                    │                     │
                    │ Spring Cloud        │
                    │ Gateway             │
                    │                     │
                    │ OAuth 2.0 / JWT     │
                    │ CORS                │
                    │ Rate Limiting       │
                    │ Routing             │
                    └──────────┬──────────┘
                               │
       ┌───────────────────────┼───────────────────────┐
       │                       │                       │
       ▼                       ▼                       ▼
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│ Auth Service │       │ Event Service│       │Booking       │
│ Member 1     │       │ Member 2     │       │Service       │
│  (:8081)     │       │  (:8082)     │       │Member 3      │
└──────┬───────┘       └──────┬───────┘       └──────┬───────┘
       │                      │                      │
       ▼                      ▼                      ▼
  Auth Database          Event Database         Booking Database


                     ┌──────────────────────┐
                     │ Payment &            │
                     │ Notification Service │
                     │ Member 4 & Member 5  │
                     │       (:8084)        │
                     └──────────┬───────────┘
                                │
                                ▼
                         Payment Database
```

---

## 3. Technology Stack

* **Java**: 21 LTS
* **Framework**: Spring Boot 3.3.5, Spring Security, Spring Data JPA, Spring Cloud Gateway
* **Authentication**: OAuth 2.0 / JWT (HMAC-SHA256), BCrypt Password Hashing
* **Internal Security**: `X-API-KEY` microservice protection
* **Database**: PostgreSQL 16 (Independent database per microservice)
* **Rate Limiting**: Redis 7
* **Documentation**: OpenAPI 3.0 / Swagger UI
* **Containerization**: Docker, Docker Compose

---

## 4. Port Allocations

| Component | Port |
| :--- | :---: |
| API Gateway | `8080` |
| User & Auth Service | `8081` |
| Event Service | `8082` |
| Booking Service | `8083` |
| Payment & Notification Service | `8084` |
| PostgreSQL (Auth DB) | `5432` |
| PostgreSQL (Event DB) | `5433` |
| PostgreSQL (Booking DB) | `5434` |
| PostgreSQL (Payment DB) | `5435` |
| Redis | `6379` |

---

## 5. Member 1 API Reference (`/auth/**` & `/users/**`)

### Authentication APIs (Public)

| Method | Endpoint | Description | Request Body | Response |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Register a new user | `{ "name": "Tharushi", "email": "tharushi@gmail.com", "password": "12345678" }` | `201 Created` |
| `POST` | `/auth/login` | Authenticate user & issue JWT | `{ "email": "tharushi@gmail.com", "password": "12345678" }` | `200 OK` + JWT Token |

### User Management APIs (Protected via JWT Bearer Token)

| Method | Endpoint | Description | Headers |
| :--- | :--- | :--- | :--- |
| `GET` | `/users` | List all users | `Authorization: Bearer <token>` |
| `GET` | `/users/{id}` | Get user by ID | `Authorization: Bearer <token>` |
| `PUT` | `/users/{id}` | Update user details | `Authorization: Bearer <token>` |
| `DELETE` | `/users/{id}` | Delete user by ID | `Authorization: Bearer <token>` |

---

## 6. Member 2 API Reference (`/events/**`)

| Method | Endpoint | Description | Request Body / Parameters |
| :--- | :--- | :--- | :--- |
| `POST` | `/events` | Create a new event | `{ "title": "Tech Conference", "location": "Colombo", "capacity": 100, "price": 2500.0 }` |
| `GET` | `/events` | List all events | None |
| `GET` | `/events/{id}` | Get event details by ID | Path variable `id` |
| `GET` | `/events/search` | Search events by title/location | Query string `?query=Colombo` |
| `GET` | `/events/{id}/availability` | Check remaining seat availability | Path variable `id` |
| `PUT` | `/events/{id}` | Update event details | Update payload |
| `DELETE` | `/events/{id}` | Delete event | Path variable `id` |

---

## 7. Member 3 API Reference (`/bookings/**`)

| Method | Endpoint | Description | Request Body / Parameters |
| :--- | :--- | :--- | :--- |
| `POST` | `/bookings` | Create a new booking (`PENDING`) | `{ "eventId": 1, "userId": 1, "tickets": 2, "unitPrice": 2500.0 }` |
| `GET` | `/bookings` | List all bookings | None |
| `GET` | `/bookings/{id}` | Get booking details by ID | Path variable `id` |
| `GET` | `/bookings/user/{userId}` | Get all bookings for a specific user | Path variable `userId` |
| `GET` | `/bookings/event/{eventId}` | Get all bookings for a specific event | Path variable `eventId` |
| `PUT` | `/bookings/{id}` | Update booking details | `{ "tickets": 3, "status": "CONFIRMED" }` |
| `POST` | `/bookings/{id}/cancel` | Cancel a booking (`CANCELLED`) | Path variable `id` |
| `POST` | `/bookings/{id}/confirm` | Confirm a booking (`CONFIRMED`) | Path variable `id` |
| `DELETE` | `/bookings/{id}` | Delete a booking | Path variable `id` |

---

## 8. Member 4 & Member 5 API Reference (`/payments/**` & `/notifications/**`)

### Member 4: Payment Management APIs (`/payments/**`)

| Method | Endpoint | Description | Request Body / Parameters |
| :--- | :--- | :--- | :--- |
| `POST` | `/payments/process` | Process booking payment | `{ "bookingId": 1, "amount": 5000.0, "paymentMethod": "CREDIT_CARD" }` |
| `GET` | `/payments` | List all payments | None |
| `GET` | `/payments/{id}` | Get payment by ID | Path variable `id` |
| `GET` | `/payments/booking/{bookingId}` | Get payment by booking ID | Path variable `bookingId` |

### Member 5: Notification & Audit Log APIs (`/notifications/**`)

| Method | Endpoint | Description | Request Body / Parameters |
| :--- | :--- | :--- | :--- |
| `POST` | `/notifications/send` | Send notification log | `{ "userId": 1, "message": "Booking Confirmed", "type": "EMAIL" }` |
| `GET` | `/notifications` | List all system notifications | None |
| `GET` | `/notifications/user/{userId}` | List notifications for user | Path variable `userId` |

---

## 9. How to Run with Docker Compose

1. Clone repository:
   ```bash
   git clone https://github.com/TharushiSalwathura/EventHub.git
   cd EventHub
   ```

2. Start all services using Docker Compose:
   ```bash
   docker compose up --build
   ```

3. Swagger Documentation URLs:
   * Auth Service Swagger: `http://localhost:8081/swagger-ui.html`
   * Event Service Swagger: `http://localhost:8082/swagger-ui.html`
   * Booking Service Swagger: `http://localhost:8083/swagger-ui.html`
   * Payment & Notification Service Swagger: `http://localhost:8084/swagger-ui.html`

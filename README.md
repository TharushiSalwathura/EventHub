# EventHub — Microservices-Based Event Booking & Management System

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.3.5](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-2023.0.3-blue.svg)](https://spring.io/projects/spring-cloud-gateway)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-29.2.1-blue.svg)](https://www.docker.com/)

## 1. Project Overview

**EventHub** is a university group project designed using a **microservices architecture** where each student owns one distinct microservice.

### Member Responsibilities

| Member | Role | Assigned Microservice | Port |
| :--- | :--- | :--- | :---: |
| **Member 1 (Lead)** | **Gateway & Auth Lead** | **User & Authentication Service + API Gateway** | **8080 / 8081** |
| Member 2 | Backend Developer | Event Management Service | 8082 |
| Member 3 | Backend Developer | Booking Service | 8083 |
| Member 4 | Backend Developer | Payment & Notification Service | 8084 |

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
             ┌─────────────────┼─────────────────┐
             │                 │                 │
             ▼                 ▼                 ▼
     ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
     │ Auth Service │  │ Event Service│  │Booking       │
     │ Member 1     │  │ Member 2     │  │Service       │
     │  (:8081)     │  │  (:8082)     │  │Member 3      │
     └──────┬───────┘  └──────┬───────┘  └──────┬───────┘
            │                 │                 │
            ▼                 ▼                 ▼
       Auth Database     Event Database    Booking Database


                         ┌──────────────────────┐
                         │ Payment & Notification│
                         │       Service        │
                         │       Member 4       │
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

## 6. How to Run with Docker Compose

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
   * Booking Service Swagger: `http://localhost:8083/swagger-ui.html`

---

## 7. Member 3 API Reference (`/bookings/**`)

### Booking Management APIs

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


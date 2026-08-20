# EventHub: A Microservices-Based Event Booking & Management System
## Group Project Technical Report
**Horizon Campus — Software Architecture Module**

### Group Members:
* **ITBIN-2313-0138** (Member 1 — Gateway & Authentication Lead)
* **ITBIN-2313-0038** (Member 2 — Event Management Service Developer)
* **ITBIN-2313-0081** (Member 3 — Booking Service Developer)
* **ITBIN-2313-0109** (Member 4 — Payment Service Developer)
* **ITBIN-2313-0123** (Member 5 — Notification Service Developer)

---

## Table of Contents
1. System Architecture & Design
   1.1 Project Overview  
   1.2 High-Level System Architecture  
   1.3 Component Responsibilities  
2. Inter-Service Communication Flow
   2.1 General Communication  
   2.2 Authentication Communication Flow  
   2.3 Event Communication Flow  
   2.4 Booking Communication Flow  
   2.5 Payment and Notification Communication Flow  
3. API Gateway Design
   3.1 Gateway Purpose  
   3.2 Gateway Routes  
4. Service Breakdown
   4.1 Member 1 — User & Authentication Service  
   4.2 Member 2 — Event Management Service  
   4.3 Member 3 — Booking Service  
   4.4 Member 4 — Payment Service  
   4.5 Member 5 — Notification Service  
5. Database Architecture (MongoDB NoSQL)  
6. Security & Infrastructure
   6.1 Authentication  
   6.2 JWT Access Tokens  
   6.3 API-Key Security  
   6.4 CORS Configuration  
   6.5 Rate Limiting (Redis)  
   6.6 Docker Containerization  
   6.7 Environment Variables  
7. Client Integration (React SPA)  
8. API Security Summary  
9. API Testing Strategy  
10. Individual Contribution Matrix  
11. Individual Contribution Details  
12. Git and Collaboration  
13. Docker Deployment Architecture  
14. Error Handling and Validation  
15. Documentation and Evidence  
16. Conclusion & Endpoint Summary  

---

## 1. System Architecture & Design

### 1.1 Project Overview
This microservices-based system developed by our group for the Software Architecture module enables users to register/login accounts, discover upcoming events, book tickets, execute mock payments, and receive real-time notifications.

#### Supported Features:
* User registration and authentication
* Event creation, listing, and full-text search
* Event ticket reservation and capacity management
* Payment processing & receipt issuance
* Real-time push notifications & audit logging

The primary motive behind selecting a microservices architecture in our system architecture was to compartmentalise the application into independent, manageable, and deployable services. Each service manages its own isolated **MongoDB NoSQL document database** (`eventhub_auth`, `eventhub_event`, `eventhub_booking`, `eventhub_payment`).

### 1.2 High-Level System Architecture
An API Gateway acts as the central entry point between the external client (React Frontend) and the internal microservices. The Gateway manages routing, authentication, CORS, Redis-based rate limiting, and forwards internal API keys (`X-API-KEY`) to backend microservices.

### 1.3 Component Responsibilities

| Component | Port | Responsibility |
| :--- | :--- | :--- |
| **React Frontend** | `3000` | Unified client Single Page Application (SPA) |
| **API Gateway** | `8080` | Central entry point, request routing, CORS, security, rate limiting |
| **Auth Service** | `8081` | User registration, login, JWT token issuance, user management |
| **Event Service** | `8082` | Event creation, listing, search, and seat availability tracking |
| **Booking Service** | `8083` | Ticket booking lifecycle (PENDING, CONFIRMED, CANCELLED) |
| **Payment & Notification Service** | `8084` | Payment processing, transaction reference generation, notifications |
| **MongoDB** | `27017–27020` | Persistent service-specific NoSQL document collections |
| **Redis** | `6379` | API Gateway rate limiting storage |

---

## 2. Inter-Service Communication Flow

### 2.1 General Communication
The client application communicates exclusively through the API Gateway on port 8080. Internal microservices communicate over Docker Compose internal network aliases (`auth-service:8081`, `event-service:8082`, `booking-service:8083`, `payment-notification-service:8084`).

### 2.2 Authentication Communication Flow
1. User enters Email & Password on the React Frontend (`:3000`).
2. Request sent to `POST /auth/login` via API Gateway (`:8080`).
3. Gateway routes request to Auth Service (`:8081`).
4. Auth Service validates credentials using BCrypt against `eventhub_auth` MongoDB collection.
5. Auth Service issues HMAC-SHA256 JWT Access Token to client.

### 2.3 Event Communication Flow
* Public endpoints (`GET /events`, `GET /events/search`) allow anonymous browsing.
* Write operations (`POST /events`, `PUT /events/{id}`, `DELETE /events/{id}`) require internal `X-API-KEY: EVENT_SERVICE_KEY`.

### 2.4 Booking Communication Flow
* Booking initialized with status **`PENDING`**.
* Following successful payment processing, booking transitions to **`CONFIRMED`**.
* If cancelled, status updates to **`CANCELLED`** and seats are returned to the event pool.

### 2.5 Payment and Notification Communication Flow
* Payment Service processes mock payment, generates transaction reference (`TXN-XXXXXXXX`), logs record in `eventhub_payment` MongoDB collection, notifies Booking Service to mark booking `CONFIRMED`, and dispatches notification.

---

## 3. API Gateway Design

### 3.1 Gateway Purpose
The API Gateway serves as the single entrance to the microservices ecosystem, performing request routing, JWT validation, CORS header injection, Redis rate limiting (10 req/sec per IP), and internal `X-API-KEY` forwarding.

### 3.2 Gateway Routes

| Gateway Route | Target Service | Container Port |
| :--- | :--- | :--- |
| `/auth/**` | Auth Service | `8081` |
| `/users/**` | Auth Service | `8081` |
| `/events/**` | Event Service | `8082` |
| `/bookings/**` | Booking Service | `8083` |
| `/payments/**` | Payment & Notification Service | `8084` |
| `/notifications/**` | Payment & Notification Service | `8084` |

---

## 4. Service Breakdown

### 4.1 Member 1 — User & Authentication Service (ITBIN-2313-0138)
* **Port**: `8081` (Service) / `8080` (Gateway)
* **Database**: MongoDB (`eventhub_auth` collection: `users`)
* **Endpoints**: `POST /auth/register`, `POST /auth/login`, `GET /users`, `GET /users/{id}`, `PUT /users/{id}`, `DELETE /users/{id}`

### 4.2 Member 2 — Event Management Service (ITBIN-2313-0038)
* **Port**: `8082`
* **Database**: MongoDB (`eventhub_event` collection: `events`)
* **Endpoints**: `POST /events`, `GET /events`, `GET /events/{id}`, `PUT /events/{id}`, `DELETE /events/{id}`, `GET /events/search`, `GET /events/{id}/availability`

### 4.3 Member 3 — Booking Service (ITBIN-2313-0081)
* **Port**: `8083`
* **Database**: MongoDB (`eventhub_booking` collection: `bookings`)
* **Endpoints**: `POST /bookings`, `GET /bookings`, `GET /bookings/{id}`, `GET /bookings/user/{userId}`, `PUT /bookings/{id}`, `DELETE /bookings/{id}`, `POST /bookings/{id}/cancel`, `POST /bookings/{id}/confirm`

### 4.4 Member 4 — Payment Service (ITBIN-2313-0109)
* **Port**: `8084`
* **Database**: MongoDB (`eventhub_payment` collection: `payments`)
* **Endpoints**: `POST /payments/process`, `GET /payments`, `GET /payments/{id}`, `GET /payments/booking/{bookingId}`

### 4.5 Member 5 — Notification Service (ITBIN-2313-0123)
* **Port**: `8084` (Co-located with Payment Service)
* **Database**: MongoDB (`eventhub_payment` collection: `notifications`)
* **Endpoints**: `POST /notifications/send`, `GET /notifications`, `GET /notifications/user/{userId}`

---

## 5. Database Architecture (MongoDB NoSQL)
The system enforces the **Database-per-Service** pattern using **MongoDB NoSQL document collections**. Direct database access across microservices is strictly prohibited; services exchange information exclusively via REST API calls.

* `auth-db`: `mongodb://auth-db:27017/eventhub_auth`
* `event-db`: `mongodb://event-db:27018/eventhub_event`
* `booking-db`: `mongodb://booking-db:27019/eventhub_booking`
* `payment-db`: `mongodb://payment-db:27020/eventhub_payment`

---

## 6. Security & Infrastructure

### 6.1 Authentication & JWT
Authentication uses Spring Security and HMAC-SHA256 JWT tokens. Passwords are hashed using BCrypt prior to saving in MongoDB.

### 6.2 Internal API-Key Security
Service-to-service communication is secured via internal `X-API-KEY` HTTP headers (`AUTH_SERVICE_KEY`, `EVENT_SERVICE_KEY`, `BOOKING_SERVICE_KEY`, `PAYMENT_NOTIFICATION_KEY`).

### 6.3 Rate Limiting
API Gateway integrates Redis (`redis:7-alpine` on port 6379) to enforce a rate limit of **10 requests per second** per client IP. Exceeding limits returns `HTTP 429 Too Many Requests`.

---

## 7. Client Integration & Evidence

### 7.1 Unified React Client
The React Single Page Application (`http://localhost:3000`) provides screens for Registration, Login, Event Browsing, Booking Modal, Card Payment Gateway, and Notification Drawer.

### 7.2 Postman & OpenAPI Testing
A complete Postman collection (`EventHub.postman_collection.json`) and environment (`EventHub.postman_environment.json`) accompany the project for testing all REST API endpoints.

---

## 8. Conclusion
EventHub successfully demonstrates a distributed microservices architecture using Spring Boot 3, Spring Cloud Gateway, MongoDB NoSQL databases, Redis rate limiting, JWT security, Docker Compose containerization, and a modern React UI.

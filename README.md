# Smart Pantry (Akıllı Kiler)

Collaborative pantry management backend built with **Java 21** and **Spring Boot**.  
Track household inventory, reduce food waste, manage shopping lists, and analyze consumption habits.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)

---

## Features (Roadmap)

| Phase | Feature | Status |
|-------|---------|--------|
| 0 | Project bootstrap | ✅ Complete |
| 1 | Authentication (JWT) | ✅ Complete |
| 2 | Household management | Planned |
| 3 | Pantry inventory | Planned |
| 4 | Expiration & notifications | Planned |
| 5 | Shopping list | Planned |
| 6 | Activity history | Planned |
| 7 | Statistics dashboard | Planned |
| 8 | Consumption analysis | Planned |
| 9 | Recipe recommendations | Planned |
| 10 | Production hardening | Planned |

---

## Tech Stack

- **Java 21** · **Spring Boot 3.4** · **Spring Security**
- **Spring Data JPA** · **Hibernate** · **PostgreSQL 16**
- **JWT (JJWT)** · **Flyway** · **MapStruct** · **Lombok** · **Bean Validation**
- **SpringDoc OpenAPI** (Swagger UI)
- **Docker** · **Docker Compose** · **Testcontainers**
- **JUnit 5** · **Mockito**

---

## Architecture

Layered architecture with clear separation of concerns:

```
Controller  →  Service  →  Repository  →  PostgreSQL
     ↓            ↓
   DTOs       Business Logic
     ↓
 MapStruct mappers
```

**Package structure:**

```
com.nehirozsari.smartpantry
├── config          # Security, JPA, OpenAPI, JWT properties
├── controller      # REST endpoints
├── domain
│   ├── entity      # JPA entities
│   └── repository  # Spring Data repositories
├── dto             # Request/response DTOs
├── exception       # Global exception handling
├── mapper          # MapStruct mappers
├── security        # JWT filter, UserPrincipal, token hashing
├── service         # Business logic
└── validation      # Custom validators
```

---

## Authentication API (Phase 1)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/auth/register` | No | Create account |
| POST | `/api/v1/auth/login` | No | Login |
| POST | `/api/v1/auth/refresh` | No | Rotate refresh token |
| POST | `/api/v1/auth/logout` | Yes | Revoke refresh token |
| PUT | `/api/v1/auth/password` | Yes | Change password |
| GET | `/api/v1/users/me` | Yes | Get profile |
| PUT | `/api/v1/users/me` | Yes | Update profile |

Refresh tokens are returned in the **JSON response body** (v1). Access tokens expire in 15 minutes; refresh tokens in 7 days. Refresh token rotation is enforced on every refresh.

---

## Prerequisites

- **Java 21+** (JDK; set `JAVA_HOME` if `mvnw` cannot find Java)
- **Maven** — included via `mvnw.cmd` (no global install required)
- **Docker & Docker Compose** (for PostgreSQL, full-stack run, and integration tests)

### Windows note

If `mvnw.cmd` reports JAVA_HOME is not set:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"   # adjust to your JDK path
.\mvnw.cmd test
```

---

## Quick Start

### Option A — Full stack with Docker Compose

```bash
docker compose up --build
```

| Service | URL |
|---------|-----|
| API | http://localhost:8080 |
| Health check | http://localhost:8080/actuator/health |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |

### Option B — Local development (PostgreSQL in Docker, app on host)

Start only the database:

```bash
docker compose up postgres -d
```

Run the application:

```bash
.\mvnw.cmd spring-boot:run
```

---

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | HTTP port |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/smart_pantry` | Database URL |
| `app.jwt.secret` | dev secret (override in prod) | HS256 signing key |
| `app.jwt.access-token-expiration` | `15m` | Access token TTL |
| `app.jwt.refresh-token-expiration` | `7d` | Refresh token TTL |

Set `JWT_SECRET` in production (minimum 32 characters).

---

## API Error Format

All errors follow a consistent structure:

```json
{
  "timestamp": "2026-06-26T14:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "path": "/api/v1/example",
  "errors": [
    { "field": "email", "message": "must be a well-formed email address" }
  ]
}
```

**Error codes:** `VALIDATION_ERROR` · `UNAUTHORIZED` · `FORBIDDEN` · `NOT_FOUND` · `CONFLICT` · `INTERNAL_ERROR`

---

## Database Migrations

| Version | Description |
|---------|-------------|
| V1 | Baseline — PostgreSQL `pgcrypto` extension |
| V2 | `users` and `refresh_tokens` tables |

---

## Running Tests

```bash
.\mvnw.cmd test
```

- **Unit tests** use in-memory H2 (no Docker required)
- **Integration tests** use Testcontainers (skipped automatically if Docker is unavailable)

---

## Project Status

**Phase 1 complete.** JWT authentication, refresh token rotation, profile management, and RBAC-ready security foundation are implemented.

**Next:** Phase 2 — Household management (create, invite, roles, permissions).

---

## License

MIT

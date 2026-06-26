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
| 1 | Authentication (JWT) | Planned |
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

- **Java 21** · **Spring Boot 3.4**
- **Spring Data JPA** · **Hibernate** · **PostgreSQL 16**
- **Flyway** · **MapStruct** · **Lombok** · **Bean Validation**
- **SpringDoc OpenAPI** (Swagger UI)
- **Docker** · **Docker Compose**
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
├── config          # JPA auditing, OpenAPI
├── domain
│   └── entity      # JPA entities (BaseEntity)
├── dto             # Request/response DTOs
├── exception       # Global exception handling
└── ...
```

**Design decisions (Phase 0):**

- `ddl-auto: validate` — schema is owned by Flyway, not Hibernate
- `open-in-view: false` — avoids lazy-loading issues in controllers
- UUID primary keys via `GenerationType.UUID`
- Standardized `ErrorResponse` across all API errors
- JWT security scheme pre-configured in OpenAPI for upcoming auth phase

---

## Prerequisites

- **Java 21+** (JDK; set `JAVA_HOME` if `mvnw` cannot find Java)
- **Maven** — included via `mvnw.cmd` (no global install required)
- **Docker & Docker Compose** (for PostgreSQL and full-stack run)

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
| `spring.flyway.enabled` | `true` | Run migrations on startup |

Docker profile (`application-docker.yml`) overrides the datasource host to `postgres`.

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

Migrations live in `src/main/resources/db/migration/` and are applied automatically by Flyway on startup.

| Version | Description |
|---------|-------------|
| V1 | Baseline — PostgreSQL `pgcrypto` extension |

---

## Running Tests

```bash
.\mvnw.cmd test
```

Tests use an in-memory H2 database with Flyway disabled (no PostgreSQL required for unit tests).

---

## Project Status

**Phase 0 complete.** The application boots, connects to PostgreSQL, runs Flyway migrations, exposes health checks and Swagger UI, and returns standardized error responses.

**Next:** Phase 1 — Authentication (register, login, JWT, refresh tokens, profile management).

---

## License

MIT

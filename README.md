# backend-haveyouprayedtherosarytoday

Separated Spring Boot backend for **HaveYouPrayedTheRosaryToday**, configured for **Java 25**.

## Requirements

- JDK 25+ (project compiles with `--release 25`)
- Maven Wrapper (included)

## Run

```bash
./mvnw spring-boot:run
```

API base URL: `http://localhost:3001/api`  
Health check: `http://localhost:3001/api/health`

## Build

```bash
./mvnw clean package
```

## Layered architecture

Each feature is organized with explicit layers:

- `api/` → controllers and request contracts
- `application/` → use-case services and orchestration
- `domain/` → core models and repository ports
- `infrastructure/` → adapters (currently in-memory boilerplate adapters)

Implemented feature modules:

- `auth`
- `users`
- `checkins`
- `prayers`

## Boilerplate endpoint surface (matching previous backend)

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/users/me`
- `PUT /api/users/me`
- `GET /api/users/me/stats`
- `GET /api/users/{id}`
- `POST /api/checkins`
- `GET /api/checkins/feed`
- `GET /api/checkins/today`
- `GET /api/checkins/my`
- `GET /api/checkins/stats`
- `GET /api/checkins/{id}`
- `POST /api/checkins/{id}/amen`
- `POST /api/checkins/{id}/comments`
- `DELETE /api/checkins/{id}`
- `POST /api/prayers`
- `GET /api/prayers`
- `GET /api/prayers/my`
- `GET /api/prayers/testimonials`
- `GET /api/prayers/{id}`
- `PUT /api/prayers/{id}`
- `POST /api/prayers/{id}/pray`
- `POST /api/prayers/{id}/answered`
- `DELETE /api/prayers/{id}`

> For this boilerplate phase, authenticated routes use header `X-User-Id` (defaults to `demo-user`).

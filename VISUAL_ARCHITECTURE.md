# Evangelizae — Visual Architecture

> Render with any Mermaid viewer (GitHub, VS Code, mermaid.live). Pairs with `ARCHITECTURE.md` (spine) and `SYSTEM_DESIGN.md` (decisions).
> **MongoDB only** + Redis. Liturgy contract `CONTRACT_SCRAPER.md:38` PostgreSQL → Mongo migration noted in `ARCHITECTURE.md:1`.

---

## 1. System Context (C4 L1)

```mermaid
C4Context
  title Evangelizae — System Context (MongoDB only)
  Person(client, "Client", "Mobile / Web app")
  Person(admin, "Admin", "Moderation / Review")
  System(backend, "Evangelizae API", "Java 21 + Spring Boot 4.1<br/>Modular Monolith + Hexagonal<br/>MongoDB (all domains) + Redis")
  System_Ext(scraper, "LiturgyScraper", "Python, cron 0 3 * * 0<br/>CNBB PRIMARY + Vatican VALIDATION")
  System_Ext(cnbb, "CNBB", "https://liturgiadiaria.edicoescnbb.com.br")
  System_Ext(vatican, "Vatican News", "https://www.vaticannews.va/pt/palavra-do-dia")
  System_Ext(google, "Google Identity", "OAuth 2.0")
  System_Ext(smtp, "Email Provider", "SMTP / SES")

  Rel(client, backend, "HTTPS /api/v1<br/>JWT, CORS", "JSON")
  Rel(admin, backend, "HTTPS /api/v1<br/>JWT", "JSON")
  Rel(scraper, cnbb, "Fetch liturgy", "HTTPS")
  Rel(scraper, vatican, "Fetch validation", "HTTPS")
  Rel(scraper, backend, "POST /internal/v1/liturgy/import<br/>Bearer LITURGY_IMPORT_TOKEN → Mongo", "JSON batch 14d")
  Rel(backend, google, "Verify ID token", "HTTPS")
  Rel(backend, smtp, "Send mail", "SMTP")

  UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")
```

---

## 2. Containers (C4 L2)

```mermaid
C4Container
  title Evangelizae — Containers (MongoDB only)

  Person(user, "Client", "Browser / App")

  System_Boundary(sys, "Evangelizae System") {
    Container(backend, "Backend", "Java 21, Spring Boot 4.1", "Modular Monolith<br/>/api/v1 + /internal/v1<br/>EvangelizaeApiApplication.java:8")
    ContainerDb(mongo, "MongoDB", "MongoDB", "ALL: liturgy, identity, prayer, social<br/>liturgical_days, users, follows…<br/>Mongock + ClientSession")
    ContainerDb(redis, "Redis", "Redis", "Rate-limit, Idempotency TTL")
    Container(scraper, "LiturgyScraper", "Python, Docker cron", "Stateless job<br/>CONTRACT_SCRAPER.md:53")
  }

  System_Ext(cnbb, "CNBB API", "PRIMARY source")
  System_Ext(vatican, "Vatican News", "VALIDATION source")

  Rel(user, backend, "GET /liturgy/today (→Mongo liturgical_days)<br/>POST /social/* (→Mongo)<br/>openapi/evangelizae-v1.openapi.yml:9", "HTTPS")
  Rel(scraper, cnbb, "GET /v2/liturgias/{date}", "HTTPS")
  Rel(scraper, vatican, "GET /palavra-do-dia/{date}", "HTML")
  Rel(scraper, backend, "POST /internal/v1/liturgy/import<br/>CONTRACT_SCRAPER.md:88", "JSON → Mongo")
  Rel(backend, mongo, "Spring Data MongoDB<br/>ClientSession transactions", "Mongo protocol")
  Rel(backend, redis, "Rate limit + Idempotency TTL", "RESP")

  UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")
```

Single `SPRING_DATA_MONGODB_URI` — no `PostgreSQL`.

---

## 3. Backend Modules (Package-by-Feature — All MongoDB)

```mermaid
graph TB
  subgraph Backend["org.evangelizae.api — Modular Monolith (MongoDB only)"]
    direction TB
    APP["app<br/>EvangelizaeApiApplication.java:8<br/>Clock ApiConfiguration.java:13<br/>MongoConfig only"]
    subgraph SHARED["shared"]
      KERNEL["kernel<br/>UserId, ApiError.java<br/>zero Spring/Mongo deps"]
      INFRA["infrastructure<br/>GlobalExceptionHandler.java:15<br/>Bearer/JWT, CORS ApiConfiguration.java:18<br/>RateLimit, Idempotency (Mongo TTL)"]
    end
    subgraph LITURGY["liturgy [MongoDB]"]
      L_DOM["domain<br/>LiturgicalDay, Reading<br/>text XOR options CONTRACT_SCRAPER.md:145"]
      L_APP["application<br/>ImportBatch ClientSession<br/>GetToday"]
      L_IN["adapters/in<br/>internal + public controllers"]
      L_OUT["adapters/out<br/>MongoLiturgicalDayDocument<br/>embedded readings"]
    end
    subgraph IDENTITY["identity [MongoDB]"]
      I_DOM["domain<br/>User, RefreshSession"]
      I_APP["application<br/>Register/Login/Refresh/OAuth/Delete"]
      I_AD["adapters<br/>MongoUserRepository<br/>Jwt, Google, Email"]
    end
    subgraph PRAYER["prayer [MongoDB]"]
      P_DOM["domain<br/>PrayerCompletion<br/>StreakCalculator"]
      P_APP["application<br/>CheckIn + Stats"]
    end
    subgraph SOCIAL["social [MongoDB] — expansion"]
      S_DOM["domain<br/>Follow, Like, Comment<br/>flexible docs"]
      S_APP["application<br/>Follow/Like/Comment/Feed"]
      S_OUT["adapters/out<br/>MongoFollow/Like/Comment"]
    end
    PLANS["spiritualplans [MongoDB]"]
    MOD["moderation [MongoDB]"]
  end

  KERNEL --> LITURGY & IDENTITY & PRAYER & SOCIAL & PLANS & MOD
  INFRA -.-> LITURGY & IDENTITY & PRAYER & SOCIAL
  APP -. wires .-> LITURGY & IDENTITY & PRAYER & SOCIAL & PLANS & MOD

  classDef kernel fill:#1f6feb,stroke:#0b3d91,color:#fff
  classDef infra fill:#8250df,stroke:#4a2b8a,color:#fff
  classDef mongo fill:#116149,stroke:#0a3d2e,color:#fff
  class KERNEL kernel
  class INFRA infra
  class LITURGY,IDENTITY,PRAYER,SOCIAL,PLANS,MOD mongo
```

**Dependency rule:** `adapters → application → domain ← ports`, `shared/kernel` only shared. All adapters are `MongoDocument + Mapper`.

---

## 4. Hexagonal Slice (single paradigm)

```mermaid
graph LR
  subgraph HEX["Hexagon — any module (liturgy / identity / social) [MongoDB]"]
    direction TB
    DOM["DOMAIN<br/>pure Java, no Spring/Mongo"]
    APP["APPLICATION<br/>use cases"]
    PORTS["PORTS<br/>Repository interfaces"]
    DOM --- APP
    APP --- PORTS
  end

  IN["adapters/in<br/>REST controllers<br/>LiturgyController.java:12"] --> APP
  PORTS --> OUT["adapters/out<br/>MongoDocument + Mapper<br/>Spring Data MongoDB<br/>ClientSession"]
  OUT --> MONGO["MongoDB<br/>single URI, all collections"]

  KERNEL["shared/kernel<br/>UserId, Clock, errors"] -.-> DOM & APP
  INFRA["shared/infrastructure<br/>filters"] -.-> IN & OUT

  classDef dom fill:#0a7a42,stroke:#055a30,color:#fff
  classDef app fill:#1f6feb,stroke:#0b3d91,color:#fff
  class DOM dom
  class APP app
```

No `JPA`. One `@Document` per collection, same pattern everywhere — simplifies onboarding.

---

## 5. Ingestion Sequence (Scraper → Backend → MongoDB)

```mermaid
sequenceDiagram
  participant CNBB as CNBB PRIMARY
  participant VAT as Vatican VALIDATION
  participant SCR as Scraper (Python)<br/>CONTRACT_SCRAPER.md:53
  participant BE as Backend<br/>POST /internal/v1/liturgy/import
  participant MG as MongoDB [liturgical_days]

  Note over SCR: cron 0 3 * * 0, range today → +13d
  loop per day (14d)
    SCR->>CNBB: GET /v2/liturgias/{date}
    CNBB-->>SCR: JSON/HTML
    SCR->>VAT: GET /palavra-do-dia/{date} (best-effort)
    VAT-->>SCR: HTML or 404
    Note over SCR: parse, cross-validate refs,<br/>sourceHash=sha256(raw) contentHash=sha256(canonical) CONTRACT_SCRAPER.md:226
  end
  SCR->>BE: POST /internal/v1/liturgy/import<br/>Bearer LITURGY_IMPORT_TOKEN CONTRACT_SCRAPER.md:90<br/>LiturgyImportRequest CONTRACT_SCRAPER.md:99
  activate BE
  BE->>BE: Bearer auth 401/403 CONTRACT_SCRAPER.md:384
  BE->>BE: validate Jakarta @NotBlank/@Valid CONTRACT_SCRAPER.md:486<br/>text XOR options CONTRACT_SCRAPER.md:145
  BE->>MG: start ClientSession + transaction CONTRACT_SCRAPER.md:414
  loop per LiturgicalDay
    BE->>MG: findById(date) in session (unique _id)
    alt not exists — Case A CONTRACT_SCRAPER.md:398
      BE->>MG: insert liturgical_days (embedded readings/sources)<br/>insert liturgical_day_versions
      Note over BE,MG: created++
    else contentHash == incoming — Case B CONTRACT_SCRAPER.md:401
      BE->>MG: update lastVerifiedAt in session
      Note over BE,MG: unchanged++ (NO-OP)
    else contentHash != incoming — Case C CONTRACT_SCRAPER.md:405
      BE->>MG: replace liturgical_days in session<br/>insert liturgical_day_versions
      Note over BE,MG: updated++
    end
  end
  BE->>MG: commitTransaction
  BE-->>SCR: 200 {importId, SUCCESS, processed, created, updated, unchanged} CONTRACT_SCRAPER.md:424
  deactivate BE
  Note over SCR: WARNING/REVIEW_REQUIRED still persisted CONTRACT_SCRAPER.md:416
```

Single `ClientSession` — no cross-DB commit.

---

## 6. Public Reads (MongoDB only)

```mermaid
sequenceDiagram
  participant CLI as Client
  participant BE as Backend
  participant MG as MongoDB

  CLI->>BE: GET /liturgy/today?timezone=America/Sao_Paulo&locale=pt-BR<br/>openapi/evangelizae-v1.openapi.yml:9
  activate BE
  BE->>BE: parse ZoneId, validate locale=pt-BR<br/>LiturgyService.java:82
  BE->>BE: date = LocalDate.ofInstant(clock.instant(), zone) LiturgyService.java:43
  BE->>MG: findById(date) liturgical_days<br/>single doc, embedded readings
  alt found
    MG-->>BE: LiturgicalDayDocument
    BE-->>CLI: 200 DailyLiturgy source.freshness=LIVE
  else not found
    BE-->>CLI: 503 LITURGY_UNAVAILABLE GlobalExceptionHandler.java:24
  end
  deactivate BE

  CLI->>BE: POST /social/follow {followeeId}<br/>JWT
  activate BE
  BE->>BE: JwtAuthFilter
  BE->>MG: insert follows {followerId, followeeId}<br/>unique {followerId:1, followeeId:1}
  alt duplicate
    MG-->>BE: DuplicateKey
    BE-->>CLI: 409 Conflict
  else ok
    MG-->>BE: ack
    BE-->>CLI: 201 Created
  end
  deactivate BE
  Note over BE,MG: Same MongoDB, same ClientSession capability for DeleteAccount.
```

---

## 7. Data Model (ER — MongoDB Only)

```mermaid
erDiagram
  direction TB
  LITURGICAL_DAYS ||--o{ LITURGICAL_DAY_VERSIONS : "history (Mongo)"

  LITURGICAL_DAYS {
    date DATE PK "_id, unique"
    celebration OBJECT "name, type, liturgicalColor CONTRACT_SCRAPER.md:129/187"
    liturgicalSeason OBJECT "name, week, liturgicalYear"
    parts OBJECT "readings[] embedded, text XOR options CONTRACT_SCRAPER.md:145"
    sources ARRAY "CNBB/VATICAN_NEWS, PRIMARY/VALIDATION CONTRACT_SCRAPER.md:159"
    validation OBJECT "VALID/WARNING/REVIEW_REQUIRED CONTRACT_SCRAPER.md:217"
    primaryContentHash TEXT "sha256 CONTRACT_SCRAPER.md:163"
    lastVerifiedAt DATE
  }
  LITURGICAL_DAY_VERSIONS {
    uuid UUID PK
    liturgicalDate DATE FK
    contentHash TEXT
    payload OBJECT "canonical snapshot"
  }

  USERS ||--o{ PRAYER_COMPLETIONS : "has"
  USERS ||--o{ REFRESH_SESSIONS : "has"
  USERS ||--o{ FOLLOWS : "follows"
  USERS ||--o{ LIKES : "likes"
  USERS ||--o{ COMMENTS : "writes"

  USERS {
    uuid UUID PK "_id"
    normalizedEmail TEXT "unique"
    googleSub TEXT "unique sparse"
    socialProfile OBJECT "bio, avatarUrl — no migration"
  }
  PRAYER_COMPLETIONS {
    uuid UUID PK
    userId UUID FK "unique userId+localDate"
    localDate DATE
  }
  FOLLOWS {
    uuid UUID PK
    followerId UUID FK "unique follower+followee"
    followeeId UUID FK
  }
  LIKES {
    uuid UUID PK
    userId UUID FK "unique user+target"
    targetType TEXT
  }
  COMMENTS {
    uuid UUID PK
    userId UUID FK
    targetType TEXT
    text TEXT
  }
  REFRESH_SESSIONS {
    uuid UUID PK
    userId UUID FK
    tokenHash TEXT "unique"
  }
```

`PostgreSQL JOINs` → `Mongo` embedded `readings/sources` — single `findById` for `GET /liturgy/today`.

---

## 8. Deployment (MongoDB only)

```mermaid
graph TB
  subgraph Internet
    U[Client<br/>Mobile/Web]
    S[Scraper Cron<br/>Python Docker<br/>0 3 * * 0]
  end

  subgraph Cloud["Cloud / Docker Host"]
    LB[Load Balancer<br/>Ingress + TLS]
    BE[Backend<br/>evangelizae-api<br/>Dockerfile:1<br/>Java 21, :8080, /api/v1<br/>MongoConfig only]
    MG[(MongoDB<br/>Atlas / managed, Mongock<br/>ALL collections, PITR)]
    RD[(Redis<br/>rate-limit + Idempotency TTL)]
    SCR[Scraper Job<br/>liturgy-scraper<br/>LITURGY_IMPORT_URL]
    ACT[Actuator<br/>/api/v1/actuator/health<br/>mongo + redis]
  end

  U -->|HTTPS GET /liturgy/today → MG liturgical_days<br/>POST /social/* → MG| LB --> BE
  BE --> MG
  BE --> RD
  BE --> ACT
  S -.-> SCR -->|POST /internal/v1/liturgy/import<br/>Bearer → MG| LB --> BE
  SCR -.->|GET| CNBB[(CNBB)]
  SCR -.->|GET| VAT[(Vatican)]

  classDef svc fill:#1f6feb,stroke:#0b3d91,color:#fff
  classDef mongo fill:#116149,stroke:#0a3d2e,color:#fff
  class BE,LB svc
  class MG mongo
  class RD fill:#8250df,stroke:#4a2b8a,color:#fff
```

**Env:** `PORT=8080` `src/main/resources/application.yml:8`, `APP_CORS_ALLOWED_ORIGINS` `.env.example:1`, `LITURGY_IMPORT_TOKEN` `CONTRACT_SCRAPER.md:610`, `SPRING_DATA_MONGODB_URI` only, `REDIS_URL`, `JWT_SECRET`, `GOOGLE_CLIENT_ID`. No `SPRING_DATASOURCE_URL`.

---

## 9. Failure & Idempotency (Mongo ClientSession)

```mermaid
stateDiagram-v2
  [*] --> BatchReceived
  BatchReceived --> Validating : Bearer + schema OK
  Validating --> Rejected400 : malformed
  Validating --> Rejected422 : domain invalid
  Validating --> TxStart : ClientSession transaction (Mongo)

  state TxStart {
    [*] --> PerDay
    PerDay --> Created : Case A — not exists CONTRACT_SCRAPER.md:398
    PerDay --> Unchanged : Case B — hash == incoming CONTRACT_SCRAPER.md:401
    PerDay --> Updated : Case C — hash != incoming CONTRACT_SCRAPER.md:405
    Created --> PerDay
    Unchanged --> PerDay
    Updated --> PerDay
    PerDay --> Commit : all days done
  }

  Commit --> Response200 : 200 {SUCCESS, created, updated, unchanged} CONTRACT_SCRAPER.md:424
  Response200 --> [*]

  note right of Unchanged
    WARNING + REVIEW_REQUIRED still persisted CONTRACT_SCRAPER.md:416
    Single DB — one commit, one rollback
  end note
```

---

## 10. How to Use This File

- `mermaid.live` → paste any block → export SVG/PNG for docs/slides.
- GitHub renders Mermaid natively in this markdown.
- Keep `ARCHITECTURE.md` as the rulebook, `SYSTEM_DESIGN.md` as the reasoning, this file as the **visual atlas**.


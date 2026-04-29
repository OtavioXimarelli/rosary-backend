# Backend/Frontend Contract Implementation Checklist

This document verifies that each backend controller matches the frontend API contract defined in `frontend docs/frontend-api-contract.md`.

---

## ✅ AuthController Checklist

**File:** `src/main/java/dev/ximarelli/rosary/backend/auth/AuthController.java`

### POST /auth/register
- [x] Endpoint exists
- [x] Response status: 201 Created
- [x] Request shape: `{ name, email, password }`
- [x] Response shape: `{ accessToken, user: UserSummary }`
- [x] UserSummary fields: ✅ id, name, email, avatarUrl, currentStreak, longestStreak, totalCheckIns

### POST /auth/login
- [x] Endpoint exists
- [x] Response status: 200 OK
- [x] Request shape: `{ email, password }`
- [x] Response shape: `{ accessToken, user: UserSummary }`
- [x] UserSummary fields: ✅ Same as register

**Status:** ✅ FULLY ALIGNED

---

## ⚠️ CheckInsController Checklist

**File:** `src/main/java/dev/ximarelli/rosary/backend/checkins/CheckInsController.java`

### POST /checkins
- [x] Endpoint exists
- [x] Response status: 201 Created
- [x] Auth required: ✅ Uses X-User-Id header
- [x] Request shape: `{ mystery, reflection, intentions, isPublic }`
- [x] Response includes:
  - [x] id (❌ **ISSUE:** Frontend expects `_id`, backend returns `id`)
  - [x] userId, userName, userAvatar
  - [x] mystery, reflection, intentions
  - [x] comments, amenCount, hasUserAmen
  - [x] createdAt
  - [x] ✅ Extra: isPublic, prayerDuration (not in contract, check if intentional)

**Response Shape Issue:**
```java
// Current: CheckInView(id, ...) 
// Needed: CheckInView with @JsonProperty("_id") on id field
```

### GET /checkins/feed
- [x] Endpoint exists
- [x] Query params: `page` (default 1), `limit` (default 20)
- [x] Auth optional: ✅ Accepts optional X-User-Id
- [x] Response:
  - [x] Items array: ✅ Returns list of CheckInView
  - [x] **❌ ISSUE:** Pagination format mismatch
    - Frontend expects: `{ checkIns, total, page, totalPages }`
    - Backend returns: `{ items, total, page, limit }`

### GET /checkins/today
- [x] Endpoint exists
- [x] Auth required: ✅ Uses X-User-Id
- [x] Response shape: ✅ `{ hasCheckedIn: boolean, checkIn: CheckInView|null }`

### GET /checkins/my
- [x] Endpoint exists
- [x] Auth required: ✅ Uses X-User-Id
- [x] Query params: `page`, `limit`
- [x] Response: **❌ Same pagination mismatch as /feed**

### GET /checkins/stats
- [x] Endpoint exists
- [x] Auth required: ✅ Uses X-User-Id
- [x] **❌ ISSUE:** Response differs from contract
  - Expected: `{ currentStreak, longestStreak, totalCheckIns, lastCheckIn, favoriteMysteries }`
  - Backend returns: `UserCheckInStats` (unknown structure - needs inspection)
  - Missing: `lastCheckIn`, `favoriteMysteries`

### GET /checkins/{id}
- [x] Endpoint exists (implementation detail, not in contract)
- [x] Returns CheckInView

### POST /checkins/{id}/amen
- [x] Endpoint exists
- [x] Auth required: ✅
- [x] **❌ ISSUE:** Response mismatch
  - Expected: `{ amenCount: 5 }`
  - Backend returns: Full CheckInView object
  - **Fix:** Return minimal response with just amenCount

### POST /checkins/{id}/comments
- [x] Endpoint exists
- [x] Auth required: ✅
- [x] Request shape: `{ text }`
- [x] Response: Full CheckInView (matches contract expectation of "updated BackendCheckIn")

### DELETE /checkins/{id}
- [x] Endpoint exists (not in contract, implementation detail)
- [x] Response status: 204 No Content

**Status:** ⚠️ PARTIALLY ALIGNED - 3 Issues (ID naming, pagination, stats fields)

---

## ⚠️ UsersController Checklist

**File:** `src/main/java/dev/ximarelli/rosary/backend/users/UsersController.java`

### GET /users/me
- [x] Endpoint exists
- [x] Auth required: ✅
- [x] Response includes:
  - [x] id, name, email, avatarUrl, bio ✅
  - [x] createdAt ✅
  - [x] **⚠️ EXTRA FIELDS (not in contract):** currentStreak, longestStreak, totalCheckIns, lastCheckIn
  - **Decision:** Remove these or mark as internal-only?

### GET /users/me/stats
- [x] Endpoint exists
- [x] Auth required: ✅
- [x] Response should include:
  - [x] currentStreak ✅
  - [x] longestStreak ✅
  - [x] totalCheckIns ✅
  - [x] **❌ Missing:** lastCheckIn
  - [x] **❌ Missing:** favoriteMysteries (array of { mystery, count })

### PUT /users/me
- [x] Endpoint exists
- [x] Auth required: ✅
- [x] Request shape: `{ name, avatarUrl, bio }`
- [x] Response shape: UserProfile (matches GET /users/me)

### GET /users/{id}
- [x] Endpoint exists (not in contract)
- [x] Returns UserProfile

**Status:** ⚠️ PARTIALLY ALIGNED - 2 Issues (extra fields, missing stats fields)

---

## ✅ PrayersController Checklist

**File:** `src/main/java/dev/ximarelli/rosary/backend/prayers/PrayersController.java`

### POST /prayers
- [x] Endpoint exists
- [x] Response status: 201 Created
- [x] Auth required: ✅
- [x] Request shape: `{ title, description, category }`
- [x] Response: PrayerRequestView
- [x] PrayerRequestView shape:
  - [x] id (❌ **ISSUE:** Needs to be `_id` in JSON)
  - [x] userId, userName, userAvatar ✅
  - [x] title, description, category ✅
  - [x] prayingForCount, isUserPraying ✅
  - [x] isActive, isAnswered, answeredAt, testimonial ✅
  - [x] createdAt ✅

### GET /prayers
- [x] Endpoint exists
- [x] Query params: `page`, `limit`, `category` (IntentionTag) ✅
- [x] Response: Paginated list
- [x] **❌ ISSUE:** Pagination format mismatch (same as check-ins)

### GET /prayers/my
- [x] Endpoint exists
- [x] Auth required: ✅
- [x] Query params: `page`, `limit`
- [x] Response: Paginated list
- [x] **❌ ISSUE:** Pagination format mismatch

### GET /prayers/testimonials
- [x] Endpoint exists
- [x] Query params: `page`, `limit`
- [x] Response: Paginated list (answered prayers)
- [x] **❌ ISSUE:** Pagination format mismatch

### GET /prayers/{id}
- [x] Endpoint exists
- [x] Response: PrayerRequestView

### PUT /prayers/{id}
- [x] Endpoint exists
- [x] Auth required: ✅
- [x] Request shape: `{ title, description, category, isActive }`
- [x] Response: Updated PrayerRequestView

### POST /prayers/{id}/pray
- [x] Endpoint exists
- [x] Auth required: ✅
- [x] Response: Updated PrayerRequestView (includes updated prayingForCount, isUserPraying)

### POST /prayers/{id}/answered
- [x] Endpoint exists
- [x] Auth required: ✅
- [x] Request shape: `{ testimonial }`
- [x] Response: Updated PrayerRequestView (isAnswered: true, answeredAt set, testimonial set)

### DELETE /prayers/{id}
- [x] Endpoint exists
- [x] Auth required: ✅
- [x] Response status: 204 No Content

**Status:** ⚠️ MOSTLY ALIGNED - 2 Issues (ID naming, pagination format)

---

## ❌ Journal Controller Checklist

**Status:** ⏳ **NOT YET IMPLEMENTED**

Required endpoints:
- [ ] POST /journal/entries (auth)
- [ ] GET /journal/entries?from=...&to=... (auth)
- [ ] PUT /journal/entries/{id} (auth)
- [ ] DELETE /journal/entries/{id} (auth)

Expected JournalEntry shape:
```json
{
  "id": "j1",
  "date": "2026-04-17T00:00:00Z",
  "content": "text",
  "mood": "peaceful",
  "tags": ["gratitude"],
  "intentions": "string",
  "mystery": "Mistérios Gozosos"
}
```

---

## Enum Mappings Checklist

### Mystery Type Enum
**File:** `src/main/java/dev/ximarelli/rosary/backend/checkins/MysteryType.java`

```java
public enum MysteryType {
    MISTERIOS_GOZOSOS,
    MISTERIOS_DOLOROSOS,
    MISTERIOS_GLORIOSOS,
    MISTERIOS_LUMINOSOS
}
```

**Frontend Contract Mappings (from enum-mapping.json):**
```
joyful → "Mistérios Gozosos"
sorrowful → "Mistérios Dolorosos"
glorious → "Mistérios Gloriosos"
luminous → "Mistérios Luminosos"
```

**Status:** ❌ **NEEDS JSON SERIALIZATION MAPPING**
- Backend enum uses CONSTANT_CASE (MISTERIOS_GOZOSOS)
- Contract expects Portuguese strings ("Mistérios Gozosos")
- **Action:** Add @JsonValue or custom serializer to convert enum to contract format

### Intention Tag Enum
**File:** `src/main/java/dev/ximarelli/rosary/backend/prayers/IntentionTag.java`

```java
public enum IntentionTag {
    FAMILIA,
    PAZ,
    SAUDE,
    TRABALHO,
    ESTUDOS,
    VOCACAO,
    CONVERSAO,
    IGREJA,
    FIEIS_DEFUNTOS,
    PESSOAL
}
```

**Frontend Contract Mappings (from enum-mapping.json):**
- family → Família → FAMILIA ✅
- peace → Paz → PAZ ✅
- health → Saúde → SAUDE ✅
- gratitude → Pessoal → PESSOAL ✅
- work → Trabalho → TRABALHO ✅
- faith → {Estudos, Vocação, Igreja} → {ESTUDOS, VOCACAO, IGREJA} ✅
- healing → Conversão → CONVERSAO ✅

**Status:** ❌ **NEEDS JSON SERIALIZATION MAPPING**
- Backend uses CONSTANT_CASE
- Contract/frontend uses lowercase camelCase or Portuguese
- Many-to-one: Multiple backend enums → one frontend enum (handle in frontend adapter)

---

## Pagination Format Standardization

### Current PagedResult Implementation
```java
public record PagedResult<T>(
    int page,
    int limit,
    long total,
    List<T> items) {
}
```

### Frontend Contract Expectations
```json
// Check-ins
{ "checkIns": [...], "total": 42, "page": 1, "totalPages": 5 }

// Prayers
{ "prayers": [...], "total": 42, "page": 1, "totalPages": 5 }

// Journal
{ "entries": [...], "total": 42, "page": 1, "totalPages": 5 }
```

### Issues to Fix
- [x] Standardize array field name? (items vs endpoint-specific name)
- [x] Add `totalPages` calculation? (currently returns `limit` instead)
- [x] Update all endpoints to match selected format

**Status:** ⚠️ NEEDS STANDARDIZATION ACROSS ALL ENDPOINTS

---

## Data Visibility Classification

### Public API Fields (Should Return)
- [x] id, userId, userName, email, name, avatarUrl
- [x] Public content timestamps (createdAt)
- [x] Stats (streak, totalCheckIns, etc.)
- [x] Public social features (amenCount, comments, prayingForCount)

### Internal-Only Fields (Should NOT Return)
- [x] `lastCheckIn` - Compute from check-ins, don't store separately
- [x] `amens` list - Internal implementation detail, frontend only needs count
- [x] Raw database internal IDs

### Fields Needing Clarity
- [x] `isPublic` on CheckInView - Is this needed in frontend?
- [x] `prayerDuration` on CheckInView - Is this a feature or internal?
- [x] `lastCheckIn` on UserProfile - Should move to stats only
- [x] `currentStreak` etc. on UserProfile - Should move to stats/summary only

---

## Summary

| Controller | Aligned | Issues | Action |
|-----------|---------|--------|--------|
| AuthController | ✅ 100% | 0 | Ready |
| CheckInsController | ⚠️ 60% | 3 | Fix ID field, pagination, stats response |
| UsersController | ⚠️ 70% | 2 | Trim fields, add missing stats |
| PrayersController | ⚠️ 80% | 2 | Fix ID field, pagination |
| JournalController | ❌ 0% | N/A | Implement all 4 endpoints |

**Overall:** 68% aligned → 100% target after fixes

---

## Implementation Priority

1. **CRITICAL (Blocks Usage)**
   - [ ] Fix ID field naming (_id in JSON)
   - [ ] Standardize pagination format
   - [ ] Add enum JSON serialization

2. **HIGH (Incomplete Contracts)**
   - [ ] Add lastCheckIn to UserStats
   - [ ] Add favoriteMysteries to UserStats
   - [ ] Fix amen endpoint response

3. **MEDIUM (Data Clarity)**
   - [ ] Trim UserProfile extra fields or mark internal
   - [ ] Clarify prayerDuration field intent
   - [ ] Clarify isPublic field visibility

4. **LOW (New Features)**
   - [ ] Implement Journal controller
   - [ ] Implement Journal service layer

---

## Test Coverage Needed

- [ ] Unit test: JSON serialization of Mystery enum
- [ ] Unit test: JSON serialization of IntentionTag enum
- [ ] Integration test: Auth endpoints match contract
- [ ] Integration test: Check-in CRUD matches contract
- [ ] Integration test: User endpoints match contract
- [ ] Integration test: Prayer endpoints match contract
- [ ] Integration test: Pagination format consistent across all endpoints
- [ ] Contract test: All enum values present and mapped

---

**Last Updated:** 2026-04-29
**Prepared by:** Backend/Frontend Contract Alignment Task

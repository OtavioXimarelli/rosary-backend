# Frontend/Backend Contract Alignment - EXECUTIVE SUMMARY

**Issue:** Ensure backend implementation aligns with frontend API contract before deeper implementation  
**Status:** ✅ Analysis Complete - Ready for Implementation  
**Date:** 2026-04-29

---

## Quick Assessment

| Category | Score | Status |
|----------|-------|--------|
| **Overall Alignment** | 68% → 100% target | ⚠️ Needs fixes |
| **Critical Issues** | 3 blocking issues | 🔴 Must fix first |
| **High Priority** | 3 incomplete fields | 🟠 Fix phase 2 |
| **Feature Coverage** | 13/14 endpoints | ⚠️ Journal missing |
| **Enum Mappings** | Defined but not serialized | 🟡 Needs config |

---

## 🔴 CRITICAL BLOCKERS (Must Fix Before Merge)

### 1. ID Field Naming: `_id` vs `id`
- **Issue:** Frontend expects `_id`, backend uses `id`
- **Impact:** Check-ins and prayers won't deserialize  
- **Affected:** CheckInView, PrayerRequestView  
- **Fix:** Add `@JsonProperty("_id")` annotation  
- **Effort:** 30 minutes

### 2. Pagination Format Mismatch
- **Issue:** Field name and structure differs from contract
- **Impact:** Frontend can't parse paginated responses
- **Affected:** All 6 paginated endpoints  
- **Fix:** Create endpoint-specific response DTOs with `totalPages`
- **Effort:** 2-3 hours

### 3. Enum Serialization Missing
- **Issue:** Backend enums (CONSTANT_CASE) vs frontend strings (Portuguese)
- **Impact:** Creating/updating check-ins and prayers fails
- **Affected:** MysteryType, IntentionTag  
- **Fix:** Add @JsonValue/@JsonCreator to enums
- **Effort:** 1 hour

**Total Critical Effort:** 4-5 hours

---

## 🟠 HIGH PRIORITY (Should Fix Phase 2)

### 4. UserStats Incomplete
- **Missing:** `lastCheckIn`, `favoriteMysteries`
- **Impact:** Stats page won't display complete information
- **Fix:** Query check-ins, compute stats
- **Effort:** 2-3 hours

### 5. Amen Endpoint Over-returning
- **Issue:** Returns full CheckInView instead of `{amenCount}`
- **Impact:** Violates contract, over-fetches data
- **Fix:** Create minimal AmenResponse DTO
- **Effort:** 1 hour

### 6. UserProfile Over-sharing Data
- **Issue:** Returns streak/checkins meant for stats endpoint
- **Impact:** Duplicates data, violates separation of concerns
- **Fix:** Remove 4 extra fields
- **Effort:** 30 minutes

**Total High Priority Effort:** 3-4 hours

---

## ⚠️ FEATURE GAPS

### 7. Journal CRUD Not Implemented
- **Status:** 0% complete (no controller/service found)
- **Needed:** POST, GET (range), PUT, DELETE endpoints
- **Effort:** 6-8 hours
- **Priority:** High (needed for full migration)

---

## ✅ WHAT'S ALREADY ALIGNED

### Working Endpoints (No Changes Needed)
- ✅ Auth register/login - correct shape and fields
- ✅ Check-in creation - request validated properly
- ✅ Check-in today endpoint - correct response format
- ✅ User profile GET/PUT - mostly aligned
- ✅ Prayer CRUD - all endpoints present
- ✅ Pagination logic - just needs format wrapper

### Strengths
- Good use of DTOs and records
- Proper validation with @NotNull
- Consistent auth header usage
- Well-structured controllers

---

## Documentation Provided

Three detailed reference documents have been created:

### 1. **CONTRACT_CHECKLIST.md** (~12KB)
   - **Purpose:** Verification checklist for each controller
   - **Use:** Track alignment status endpoint-by-endpoint
   - **Covers:** Request/response shapes, field validation, enum mappings
   - **Location:** `/CONTRACT_CHECKLIST.md`

### 2. **DISCREPANCIES_AND_MAPPING.md** (~22KB)
   - **Purpose:** Detailed analysis of every mismatch
   - **Use:** Implementation guide with code examples
   - **Covers:** Root causes, solution options, effort estimates
   - **Includes:** Code snippets for all fixes
   - **Location:** `/DISCREPANCIES_AND_MAPPING.md`

### 3. **CONTRACT_ALIGNMENT_ANALYSIS.md** (session artifact)
   - **Purpose:** High-level overview and context
   - **Use:** Understanding the full scope of work
   - **Location:** Session workspace (for planning reference)

---

## Implementation Roadmap

### Phase 1: Critical Fixes (4-5 hours)
✓ Unblocks all API calls
```
Priority: 🔴 MUST DO
Timeline: Before any deeper work
Tasks:
  [ ] Add @JsonProperty to ID fields
  [ ] Create pagination DTOs for each endpoint type
  [ ] Add enum serialization with @JsonValue/@JsonCreator
Testing: Manual API calls should work
```

### Phase 2: High Priority (3-4 hours)
✓ Completes missing data
```
Priority: 🟠 SHOULD DO
Timeline: Same sprint as Phase 1
Tasks:
  [ ] Add missing UserStats fields
  [ ] Fix amen endpoint response
  [ ] Trim UserProfile
Testing: Stats page displays correctly
```

### Phase 3: Feature Implementation (6-8 hours)
✓ Completes contract requirements
```
Priority: ⚠️ CRITICAL
Timeline: Next sprint or current if time available
Tasks:
  [ ] Create Journal entity and repository
  [ ] Implement JournalApplicationService
  [ ] Create JournalController with 4 endpoints
  [ ] Add date range filtering
Testing: Full CRUD operations work
```

### Phase 4: Polish & Testing (1-2 hours)
✓ Production readiness
```
Priority: 🔵 NICE TO HAVE
Timeline: Before release
Tasks:
  [ ] Add integration tests for all endpoints
  [ ] Update OpenAPI spec
  [ ] Verify all enum values match mappings
Testing: Full test suite passes
```

---

## Field Mapping Reference

### Public API Fields
These SHOULD be in responses:
- id (sent as `_id` in JSON)
- userId, userName, email, name
- avatarUrl, bio
- Public content (reflection, intentions, etc.)
- Timestamps (createdAt, etc.)
- Stats (currentStreak, totalCheckIns, etc.)
- Social metrics (amenCount, prayingForCount)

### Internal-Only Fields
These should NOT be in responses:
- lastCheckIn (compute from check-in list)
- amens list (only return count)
- Raw internal IDs
- Internal state fields
- Database-specific fields

### Fields to Clarify
- isPublic on CheckInView: Is this public API data?
- prayerDuration on CheckInView: Feature or internal?
- userAvatar: Check null serialization

---

## Enum Mapping Reference

### MysteryType
```
Backend Enum         → JSON Serialization
MISTERIOS_GOZOSOS    → "Mistérios Gozosos"
MISTERIOS_DOLOROSOS  → "Mistérios Dolorosos"
MISTERIOS_GLORIOSOS  → "Mistérios Gloriosos"
MISTERIOS_LUMINOSOS  → "Mistérios Luminosos"
```

### IntentionTag
```
Backend Enum    → JSON Serialization
FAMILIA         → "Família"
PAZ             → "Paz"
SAUDE           → "Saúde"
TRABALHO        → "Trabalho"
ESTUDOS         → "Estudos"
VOCACAO         → "Vocação"
CONVERSAO       → "Conversão"
IGREJA          → "Igreja"
FIEIS_DEFUNTOS  → "Fiéis Defuntos"
PESSOAL         → "Pessoal"
```

**Note:** Many-to-one mapping exists (multiple backend enums → one frontend enum "faith"). Handle in frontend adapter.

---

## Success Criteria

All items below must be true to declare alignment complete:

- [ ] All endpoints accessible and return correct status codes
- [ ] All requests parse without validation errors
- [ ] All responses deserialize to correct frontend models
- [ ] No field name mismatches (_id, amenCount, etc.)
- [ ] Pagination format consistent: `{items, total, page, totalPages}`
- [ ] All enum values serialize to Portuguese display names
- [ ] No undocumented or extra fields in public API responses
- [ ] Stats endpoint returns all required data
- [ ] Journal endpoints implemented and working
- [ ] Integration tests pass for all endpoints

---

## Effort Summary

| Phase | Duration | Effort |
|-------|----------|--------|
| Phase 1: Critical Fixes | 4-5h | Small-Medium |
| Phase 2: High Priority | 3-4h | Small |
| Phase 3: Features | 6-8h | Medium |
| Phase 4: Polish | 1-2h | Small |
| **Total** | **14-19h** | **Medium** |

**Recommended Allocation:** 2 developers, 2-3 days

---

## Key Decisions Made

1. **ID Field:** Use @JsonProperty("_id") to maintain MongoDB convention while using Java convention internally
2. **Pagination:** Create endpoint-specific DTOs for type safety and clear contracts
3. **Enums:** Use @JsonValue/@JsonCreator for bidirectional mapping without extra config
4. **Stats:** Compute `favoriteMysteries` on-demand from check-ins (no new persistence)
5. **Journal:** Implement as new feature in same sprint (blocks full migration)

---

## Next Steps for Team

1. ✅ **Review** this summary with backend team
2. ✅ **Decide** on implementation approach (should match recommendations)
3. ⏳ **Implement** Phase 1 (blockers) first
4. ⏳ **Test** each phase against frontend contract
5. ⏳ **Deploy** when all phases complete and tests pass

---

## Questions & Clarifications Needed

Before implementation, confirm with product/frontend teams:

1. **Is `isPublic` a public API field?** (Currently on CheckInView)
2. **Is `prayerDuration` a feature?** (Currently on CheckInView)
3. **Should `userAvatar` be null or empty string?** (Serialization consistency)
4. **What are default values for optional create fields?** (reflection, intentions, isPublic)
5. **Is Journal high priority?** (6-8 hours of effort)

---

## Reference Documents

For detailed information, see:

- **CONTRACT_CHECKLIST.md** - Endpoint-by-endpoint verification
- **DISCREPANCIES_AND_MAPPING.md** - Detailed fixes with code examples  
- **frontend docs/frontend-api-contract.md** - Original contract source
- **frontend docs/enum-mapping.json** - Canonical enum mappings

---

**Status:** ✅ Ready to Implement  
**Maintainer:** Backend/Frontend Contract Alignment Task  
**Last Updated:** 2026-04-29

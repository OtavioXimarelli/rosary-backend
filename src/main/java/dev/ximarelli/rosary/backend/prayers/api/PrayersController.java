package dev.ximarelli.rosary.backend.prayers.api;

import dev.ximarelli.rosary.backend.prayers.application.PrayerApplicationService;
import dev.ximarelli.rosary.backend.prayers.application.PrayerRequestView;
import dev.ximarelli.rosary.backend.prayers.domain.IntentionTag;
import dev.ximarelli.rosary.backend.shared.application.PagedResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrayersController {

    private final PrayerApplicationService prayerService;

    public PrayersController(PrayerApplicationService prayerService) {
        this.prayerService = prayerService;
    }

    @PostMapping("/prayers")
    @ResponseStatus(HttpStatus.CREATED)
    public PrayerRequestView create(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreatePrayerRequest request) {
        return prayerService.create(resolveUser(userId), request.title(), request.description(), request.category());
    }

    @GetMapping("/prayers")
    public PagedResult<PrayerRequestView> findAll(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) IntentionTag category) {
        return prayerService.findAll(page, limit, category, resolveUser(userId));
    }

    @GetMapping("/prayers/my")
    public PagedResult<PrayerRequestView> getMine(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        String resolved = resolveUser(userId);
        return prayerService.findMine(resolved, page, limit);
    }

    @GetMapping("/prayers/testimonials")
    public PagedResult<PrayerRequestView> getTestimonials(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return prayerService.testimonials(page, limit, resolveUser(userId));
    }

    @GetMapping("/prayers/{id}")
    public PrayerRequestView getById(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return prayerService.findById(id, resolveUser(userId));
    }

    @PutMapping("/prayers/{id}")
    public PrayerRequestView update(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody UpdatePrayerRequest request) {
        return prayerService.update(
                id,
                resolveUser(userId),
                request.title(),
                request.description(),
                request.category(),
                request.isActive());
    }

    @PostMapping("/prayers/{id}/pray")
    public PrayerRequestView togglePray(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return prayerService.togglePrayingFor(id, resolveUser(userId));
    }

    @PostMapping("/prayers/{id}/answered")
    public PrayerRequestView markAnswered(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody MarkAnsweredRequest request) {
        return prayerService.markAnswered(id, resolveUser(userId), request.testimonial());
    }

    @DeleteMapping("/prayers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        prayerService.delete(id, resolveUser(userId));
    }

    private String resolveUser(String userId) {
        return userId == null || userId.isBlank() ? "demo-user" : userId;
    }
}

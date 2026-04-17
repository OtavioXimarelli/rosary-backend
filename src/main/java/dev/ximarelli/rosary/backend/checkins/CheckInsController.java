package dev.ximarelli.rosary.backend.checkins;

import dev.ximarelli.rosary.backend.checkins.CheckInApplicationService;
import dev.ximarelli.rosary.backend.checkins.CheckInView;
import dev.ximarelli.rosary.backend.checkins.UserCheckInStats;
import dev.ximarelli.rosary.backend.shared.PagedResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CheckInsController {

    private final CheckInApplicationService checkInService;

    public CheckInsController(CheckInApplicationService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/checkins")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckInView create(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateCheckInRequest request) {
        return checkInService.create(
                resolveUser(userId),
                request.mystery(),
                request.reflection(),
                request.intentions(),
                request.isPublic(),
                request.prayerDuration());
    }

    @GetMapping("/checkins/feed")
    public PagedResult<CheckInView> getFeed(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return checkInService.getPublicFeed(page, limit, resolveUser(userId));
    }

    @GetMapping("/checkins/today")
    public Map<String, Object> getToday(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        CheckInView checkIn = checkInService.getToday(resolveUser(userId));
        return Map.of("hasCheckedIn", checkIn != null, "checkIn", checkIn);
    }

    @GetMapping("/checkins/my")
    public PagedResult<CheckInView> getMine(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        String resolved = resolveUser(userId);
        return checkInService.getUserCheckIns(resolved, page, limit);
    }

    @GetMapping("/checkins/stats")
    public UserCheckInStats getStats(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        return checkInService.getStats(resolveUser(userId));
    }

    @GetMapping("/checkins/{id}")
    public CheckInView getById(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return checkInService.getById(id, resolveUser(userId));
    }

    @PostMapping("/checkins/{id}/amen")
    public CheckInView toggleAmen(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return checkInService.toggleAmen(id, resolveUser(userId));
    }

    @PostMapping("/checkins/{id}/comments")
    public CheckInView addComment(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody AddCommentRequest request) {
        return checkInService.addComment(id, resolveUser(userId), request.text());
    }

    @DeleteMapping("/checkins/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        checkInService.delete(id, resolveUser(userId));
    }

    private String resolveUser(String userId) {
        return userId == null || userId.isBlank() ? "demo-user" : userId;
    }
}

package dev.ximarelli.rosary.backend.journal;

import dev.ximarelli.rosary.backend.config.CurrentUser;
import dev.ximarelli.rosary.backend.shared.PagedResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class JournalController {

    private final JournalApplicationService journalService;

    public JournalController(JournalApplicationService journalService) {
        this.journalService = journalService;
    }

    @PostMapping("/journal/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public JournalEntryView create(
            @CurrentUser String userId,
            @Valid @RequestBody CreateJournalEntryRequest request) {
        return journalService.create(
                userId,
                request.date(),
                request.content(),
                request.mood(),
                request.tags(),
                request.intentions(),
                request.mystery());
    }

    @GetMapping("/journal/entries")
    public PagedResult<JournalEntryView> getEntries(
            @CurrentUser String userId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return journalService.findByUser(userId, from, to, page, limit);
    }

    @PutMapping("/journal/entries/{id}")
    public JournalEntryView update(
            @PathVariable String id,
            @CurrentUser String userId,
            @Valid @RequestBody UpdateJournalEntryRequest request) {
        return journalService.update(
                id,
                userId,
                request.date(),
                request.content(),
                request.mood(),
                request.tags(),
                request.intentions(),
                request.mystery());
    }

    @DeleteMapping("/journal/entries/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String id,
            @CurrentUser String userId) {
        journalService.delete(id, userId);
    }
}

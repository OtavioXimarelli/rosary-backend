package dev.ximarelli.rosary.backend.journal;

import dev.ximarelli.rosary.backend.checkins.MysteryType;
import dev.ximarelli.rosary.backend.shared.PagedResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JournalApplicationService {

    private final JournalEntryRepository journalEntryRepository;

    public JournalApplicationService(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    public JournalEntryView create(
            String userId,
            Instant date,
            String content,
            String mood,
            List<String> tags,
            String intentions,
            MysteryType mystery) {
        Instant now = Instant.now();
        JournalEntry saved = journalEntryRepository.save(new JournalEntry(
                null,
                userId,
                date != null ? date : now,
                content,
                mood,
                tags == null ? List.of() : tags,
                intentions,
                mystery,
                now,
                now));
        return toView(saved);
    }

    public PagedResult<JournalEntryView> findByUser(String userId, Instant from, Instant to, int page, int limit) {
        List<JournalEntry> filtered = journalEntryRepository.findByUserIdOrderByDateDesc(userId).stream()
                .filter(entry -> from == null || !entry.date().isBefore(from))
                .filter(entry -> to == null || !entry.date().isAfter(to))
                .toList();

        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);
        int fromIndex = Math.min((safePage - 1) * safeLimit, filtered.size());
        int toIndex = Math.min(fromIndex + safeLimit, filtered.size());
        List<JournalEntryView> items = filtered.subList(fromIndex, toIndex).stream().map(this::toView).toList();

        return new PagedResult<>(safePage, safeLimit, filtered.size(), items);
    }

    public JournalEntryView update(
            String id,
            String userId,
            Instant date,
            String content,
            String mood,
            List<String> tags,
            String intentions,
            MysteryType mystery) {
        JournalEntry current = journalEntryRepository.findById(id).orElseThrow();
        if (!current.userId().equals(userId)) {
            throw new IllegalArgumentException("Cannot update another user's journal entry");
        }

        JournalEntry updated = journalEntryRepository.save(new JournalEntry(
                current.id(),
                current.userId(),
                date != null ? date : current.date(),
                content != null ? content : current.content(),
                mood != null ? mood : current.mood(),
                tags != null ? tags : current.tags(),
                intentions != null ? intentions : current.intentions(),
                mystery != null ? mystery : current.mystery(),
                current.createdAt(),
                Instant.now()));

        return toView(updated);
    }

    public void delete(String id, String userId) {
        JournalEntry current = journalEntryRepository.findById(id).orElseThrow();
        if (!current.userId().equals(userId)) {
            throw new IllegalArgumentException("Cannot delete another user's journal entry");
        }
        journalEntryRepository.deleteById(id);
    }

    private JournalEntryView toView(JournalEntry entry) {
        return new JournalEntryView(
                entry.id(),
                entry.date(),
                entry.content(),
                entry.mood(),
                entry.tags(),
                entry.intentions(),
                entry.mystery());
    }
}

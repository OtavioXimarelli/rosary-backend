package dev.ximarelli.rosary.backend.journal;

import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryJournalEntryRepository implements JournalEntryRepository {

    private final Map<String, JournalEntry> entriesById = new ConcurrentHashMap<>();

    @Override
    public JournalEntry save(JournalEntry entry) {
        String id = entry.id() != null ? entry.id() : UUID.randomUUID().toString();
        JournalEntry persisted = new JournalEntry(
                id,
                entry.userId(),
                entry.date(),
                entry.content(),
                entry.mood(),
                entry.tags(),
                entry.intentions(),
                entry.mystery(),
                entry.createdAt(),
                entry.updatedAt());
        entriesById.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<JournalEntry> findById(String id) {
        return Optional.ofNullable(entriesById.get(id));
    }

    @Override
    public List<JournalEntry> findByUserIdOrderByDateDesc(String userId) {
        return entriesById.values().stream()
                .filter(entry -> entry.userId().equals(userId))
                .sorted(Comparator.comparing(JournalEntry::date).reversed()
                        .thenComparing(JournalEntry::createdAt, Comparator.reverseOrder()))
                .toList();
    }

    @Override
    public void deleteById(String id) {
        entriesById.remove(id);
    }
}

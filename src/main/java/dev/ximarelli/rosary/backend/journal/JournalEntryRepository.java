package dev.ximarelli.rosary.backend.journal;

import java.util.List;
import java.util.Optional;

public interface JournalEntryRepository {
    JournalEntry save(JournalEntry entry);

    Optional<JournalEntry> findById(String id);

    List<JournalEntry> findByUserIdOrderByDateDesc(String userId);

    void deleteById(String id);
}

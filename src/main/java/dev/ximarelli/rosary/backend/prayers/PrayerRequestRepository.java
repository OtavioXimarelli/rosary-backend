package dev.ximarelli.rosary.backend.prayers;

import dev.ximarelli.rosary.backend.prayers.PrayerRequest;

import java.util.List;
import java.util.Optional;

public interface PrayerRequestRepository {
    PrayerRequest save(PrayerRequest prayerRequest);
    Optional<PrayerRequest> findById(String id);
    List<PrayerRequest> findAll();
    List<PrayerRequest> findByUser(String userId);
    void deleteById(String id);
}

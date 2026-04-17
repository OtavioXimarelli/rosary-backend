package dev.ximarelli.rosary.backend.prayers;

import dev.ximarelli.rosary.backend.prayers.PrayerRequest;
import dev.ximarelli.rosary.backend.prayers.PrayerRequestRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPrayerRequestRepository implements PrayerRequestRepository {

    private final Map<String, PrayerRequest> prayersById = new ConcurrentHashMap<>();

    @Override
    public PrayerRequest save(PrayerRequest prayerRequest) {
        String id = prayerRequest.id() != null ? prayerRequest.id() : UUID.randomUUID().toString();
        PrayerRequest persisted = new PrayerRequest(
                id,
                prayerRequest.userId(),
                prayerRequest.userName(),
                prayerRequest.userAvatar(),
                prayerRequest.title(),
                prayerRequest.description(),
                prayerRequest.category(),
                prayerRequest.prayingFor(),
                prayerRequest.isActive(),
                prayerRequest.isAnswered(),
                prayerRequest.answeredAt(),
                prayerRequest.testimonial(),
                prayerRequest.createdAt());
        prayersById.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<PrayerRequest> findById(String id) {
        return Optional.ofNullable(prayersById.get(id));
    }

    @Override
    public List<PrayerRequest> findAll() {
        return prayersById.values().stream()
                .sorted(Comparator.comparing(PrayerRequest::createdAt).reversed())
                .toList();
    }

    @Override
    public List<PrayerRequest> findByUser(String userId) {
        return prayersById.values().stream()
                .filter(it -> it.userId().equals(userId))
                .sorted(Comparator.comparing(PrayerRequest::createdAt).reversed())
                .toList();
    }

    @Override
    public void deleteById(String id) {
        prayersById.remove(id);
    }
}

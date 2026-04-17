package dev.ximarelli.rosary.backend.checkins;

import dev.ximarelli.rosary.backend.checkins.CheckIn;
import dev.ximarelli.rosary.backend.checkins.CheckInRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCheckInRepository implements CheckInRepository {

    private final Map<String, CheckIn> checkInsById = new ConcurrentHashMap<>();

    @Override
    public CheckIn save(CheckIn checkIn) {
        String id = checkIn.id() != null ? checkIn.id() : UUID.randomUUID().toString();
        CheckIn persisted = new CheckIn(
                id,
                checkIn.userId(),
                checkIn.userName(),
                checkIn.userAvatar(),
                checkIn.mystery(),
                checkIn.reflection(),
                checkIn.intentions(),
                checkIn.amens(),
                checkIn.comments(),
                checkIn.isPublic(),
                checkIn.prayerDuration(),
                checkIn.createdAt());
        checkInsById.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<CheckIn> findById(String id) {
        return Optional.ofNullable(checkInsById.get(id));
    }

    @Override
    public Optional<CheckIn> findByUserAndDay(String userId, LocalDate day) {
        return checkInsById.values().stream()
                .filter(it -> it.userId().equals(userId))
                .filter(it -> LocalDate.ofInstant(it.createdAt(), ZoneOffset.UTC).isEqual(day))
                .findFirst();
    }

    @Override
    public List<CheckIn> findByUser(String userId) {
        return checkInsById.values().stream()
                .filter(it -> it.userId().equals(userId))
                .sorted(Comparator.comparing(CheckIn::createdAt).reversed())
                .toList();
    }

    @Override
    public List<CheckIn> findPublic() {
        return checkInsById.values().stream()
                .filter(CheckIn::isPublic)
                .sorted(Comparator.comparing(CheckIn::createdAt).reversed())
                .toList();
    }

    @Override
    public void deleteById(String id) {
        checkInsById.remove(id);
    }
}

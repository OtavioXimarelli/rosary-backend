package dev.ximarelli.rosary.backend.checkins;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record CheckIn(
        String id,
        String userId,
        String userName,
        String userAvatar,
        MysteryType mystery,
        String reflection,
        List<String> intentions,
        Set<String> amens,
        List<CheckInComment> comments,
        boolean isPublic,
        Integer prayerDuration,
        Instant createdAt) {
}

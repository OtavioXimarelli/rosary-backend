package dev.ximarelli.rosary.backend.prayers;

import java.time.Instant;
import java.util.Set;

public record PrayerRequest(
        String id,
        String userId,
        String userName,
        String userAvatar,
        String title,
        String description,
        IntentionTag category,
        Set<String> prayingFor,
        boolean isActive,
        boolean isAnswered,
        Instant answeredAt,
        String testimonial,
        Instant createdAt) {
}

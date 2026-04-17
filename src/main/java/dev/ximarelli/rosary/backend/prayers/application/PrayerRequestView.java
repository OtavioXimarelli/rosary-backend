package dev.ximarelli.rosary.backend.prayers.application;

import dev.ximarelli.rosary.backend.prayers.domain.IntentionTag;

import java.time.Instant;

public record PrayerRequestView(
        String id,
        String userId,
        String userName,
        String userAvatar,
        String title,
        String description,
        IntentionTag category,
        int prayingForCount,
        boolean isUserPraying,
        boolean isActive,
        boolean isAnswered,
        Instant answeredAt,
        String testimonial,
        Instant createdAt) {
}

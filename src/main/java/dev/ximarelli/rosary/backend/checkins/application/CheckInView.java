package dev.ximarelli.rosary.backend.checkins.application;

import dev.ximarelli.rosary.backend.checkins.domain.MysteryType;

import java.time.Instant;
import java.util.List;

public record CheckInView(
        String id,
        String userId,
        String userName,
        String userAvatar,
        MysteryType mystery,
        String reflection,
        List<String> intentions,
        List<String> amens,
        int amenCount,
        List<CommentView> comments,
        boolean isPublic,
        Integer prayerDuration,
        Instant createdAt,
        boolean hasUserAmen) {
}

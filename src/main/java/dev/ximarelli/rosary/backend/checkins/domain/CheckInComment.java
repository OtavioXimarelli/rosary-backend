package dev.ximarelli.rosary.backend.checkins.domain;

import java.time.Instant;

public record CheckInComment(
        String userId,
        String userName,
        String userAvatar,
        String text,
        Instant createdAt) {
}

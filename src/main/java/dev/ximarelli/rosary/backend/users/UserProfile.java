package dev.ximarelli.rosary.backend.users;

import java.time.Instant;

public record UserProfile(
        String id,
        String name,
        String email,
        String avatarUrl,
        String bio,
        int currentStreak,
        int longestStreak,
        int totalCheckIns,
        Instant lastCheckIn,
        Instant createdAt) {
}

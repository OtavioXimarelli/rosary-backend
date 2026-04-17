package dev.ximarelli.rosary.backend.users;

import java.time.Instant;

public record User(
        String id,
        String name,
        String email,
        String passwordHash,
        String avatarUrl,
        String bio,
        int currentStreak,
        int longestStreak,
        int totalCheckIns,
        Instant lastCheckIn,
        Instant createdAt) {

    public User withProfile(String newName, String newAvatarUrl, String newBio) {
        return new User(
                id,
                newName != null ? newName : name,
                email,
                passwordHash,
                newAvatarUrl != null ? newAvatarUrl : avatarUrl,
                newBio != null ? newBio : bio,
                currentStreak,
                longestStreak,
                totalCheckIns,
                lastCheckIn,
                createdAt);
    }
}

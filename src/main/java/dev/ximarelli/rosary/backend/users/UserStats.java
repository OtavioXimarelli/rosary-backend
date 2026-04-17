package dev.ximarelli.rosary.backend.users;

public record UserStats(
        int currentStreak,
        int longestStreak,
        int totalCheckIns) {
}

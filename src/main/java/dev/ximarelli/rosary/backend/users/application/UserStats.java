package dev.ximarelli.rosary.backend.users.application;

public record UserStats(
        int currentStreak,
        int longestStreak,
        int totalCheckIns) {
}

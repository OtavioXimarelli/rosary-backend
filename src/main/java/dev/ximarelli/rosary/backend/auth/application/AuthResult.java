package dev.ximarelli.rosary.backend.auth.application;

import dev.ximarelli.rosary.backend.users.application.UserSummary;

public record AuthResult(
        String accessToken,
        UserSummary user) {
}

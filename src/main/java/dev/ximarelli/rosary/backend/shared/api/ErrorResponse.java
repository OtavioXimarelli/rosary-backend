package dev.ximarelli.rosary.backend.shared.api;

import java.time.Instant;

public record ErrorResponse(
        String message,
        Instant timestamp) {
}

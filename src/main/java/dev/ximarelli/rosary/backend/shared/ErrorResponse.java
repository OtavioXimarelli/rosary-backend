package dev.ximarelli.rosary.backend.shared;

import java.time.Instant;

public record ErrorResponse(
        String message,
        Instant timestamp) {
}

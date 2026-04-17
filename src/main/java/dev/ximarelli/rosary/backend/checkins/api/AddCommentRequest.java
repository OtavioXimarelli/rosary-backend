package dev.ximarelli.rosary.backend.checkins.api;

import jakarta.validation.constraints.NotBlank;

public record AddCommentRequest(
        @NotBlank String text) {
}

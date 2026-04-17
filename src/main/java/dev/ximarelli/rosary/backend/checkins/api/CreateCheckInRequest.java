package dev.ximarelli.rosary.backend.checkins.api;

import dev.ximarelli.rosary.backend.checkins.domain.MysteryType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateCheckInRequest(
        @NotNull MysteryType mystery,
        String reflection,
        List<String> intentions,
        Boolean isPublic,
        @Min(1) @Max(180) Integer prayerDuration) {
}

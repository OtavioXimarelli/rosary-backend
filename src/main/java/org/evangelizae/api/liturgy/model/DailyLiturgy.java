package org.evangelizae.api.liturgy.model;

import java.time.LocalDate;
import java.util.List;

public record DailyLiturgy(
        LocalDate date,
        String title,
        LiturgicalColor color,
        LiturgyPrayers prayers,
        List<LiturgyGroup> groups,
        LiturgySource source
) {
}


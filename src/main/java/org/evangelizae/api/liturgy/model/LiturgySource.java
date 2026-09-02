package org.evangelizae.api.liturgy.model;

import java.time.Instant;

public record LiturgySource(
        String provider,
        Instant fetchedAt,
        Freshness freshness
) {
    public enum Freshness {
        LIVE,
        CACHED
    }
}


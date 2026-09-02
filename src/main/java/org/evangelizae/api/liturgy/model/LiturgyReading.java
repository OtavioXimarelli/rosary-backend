package org.evangelizae.api.liturgy.model;

public record LiturgyReading(
        String title,
        String reference,
        String text,
        String refrain
) {
}


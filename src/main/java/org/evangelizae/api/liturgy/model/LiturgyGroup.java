package org.evangelizae.api.liturgy.model;

import java.util.List;

public record LiturgyGroup(ReadingKind kind, List<LiturgyReading> items) {
}


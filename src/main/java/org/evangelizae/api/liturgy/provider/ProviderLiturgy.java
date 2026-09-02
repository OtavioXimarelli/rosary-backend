package org.evangelizae.api.liturgy.provider;

import java.time.LocalDate;
import java.util.List;

import org.evangelizae.api.liturgy.model.LiturgicalColor;
import org.evangelizae.api.liturgy.model.LiturgyGroup;
import org.evangelizae.api.liturgy.model.LiturgyPrayers;

public record ProviderLiturgy(LocalDate date, String title, LiturgicalColor color,
        LiturgyPrayers prayers, List<LiturgyGroup> groups) {}

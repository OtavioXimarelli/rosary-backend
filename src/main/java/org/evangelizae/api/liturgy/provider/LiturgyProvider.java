package org.evangelizae.api.liturgy.provider;

import java.time.LocalDate;

public interface LiturgyProvider {
    ProviderLiturgy fetch(LocalDate date, String locale);
}

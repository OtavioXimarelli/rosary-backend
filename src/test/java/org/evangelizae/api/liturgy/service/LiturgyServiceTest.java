package org.evangelizae.api.liturgy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.evangelizae.api.config.AppProperties;
import org.evangelizae.api.liturgy.model.LiturgicalColor;
import org.evangelizae.api.liturgy.model.LiturgyGroup;
import org.evangelizae.api.liturgy.model.LiturgyPrayers;
import org.evangelizae.api.liturgy.model.LiturgyReading;
import org.evangelizae.api.liturgy.model.LiturgySource;
import org.evangelizae.api.liturgy.model.ReadingKind;
import org.evangelizae.api.liturgy.provider.LiturgyProvider;
import org.evangelizae.api.liturgy.provider.ProviderLiturgy;
import org.evangelizae.api.liturgy.provider.ProviderUnavailableException;
import org.junit.jupiter.api.Test;

class LiturgyServiceTest {

    private static final Instant NOW = Instant.parse("2026-08-24T14:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void returnsValidatedLiveLiturgy() {
        var service = service((date, locale) -> validLiturgy(date));

        var response = service.getToday("America/Sao_Paulo", "pt-BR");

        assertThat(response.date().toString()).isEqualTo("2026-08-24");
        assertThat(response.source().provider()).isEqualTo("test-provider");
        assertThat(response.source().freshness()).isEqualTo(LiturgySource.Freshness.LIVE);
    }

    @Test
    void usesOnlyTheSameDateCacheWhenProviderFails() {
        var provider = new SwitchingProvider();
        var service = service(provider);
        service.getToday("America/Sao_Paulo", "pt-BR");
        provider.fail = true;

        var response = service.getToday("America/Sao_Paulo", "pt-BR");

        assertThat(response.source().freshness()).isEqualTo(LiturgySource.Freshness.CACHED);
        assertThat(response.date().toString()).isEqualTo("2026-08-24");
    }

    @Test
    void rejectsWrongDateProviderContentInsteadOfPresentingItAsToday() {
        var service = service((date, locale) -> validLiturgy(date.minusDays(1)));

        assertThatThrownBy(() -> service.getToday("America/Sao_Paulo", "pt-BR"))
                .isInstanceOf(LiturgyUnavailableException.class);
    }

    @Test
    void rejectsUnsupportedLocale() {
        var service = service((date, locale) -> validLiturgy(date));

        assertThatThrownBy(() -> service.getToday("America/Sao_Paulo", "en-US"))
                .isInstanceOf(InvalidLiturgyRequestException.class)
                .hasMessage("locale must be pt-BR");
    }

    private LiturgyService service(LiturgyProvider provider) {
        var providerProperties = new AppProperties.Liturgy.Provider(
                true,
                "https://provider.example/liturgy",
                "test-provider",
                java.time.Duration.ofSeconds(1),
                java.time.Duration.ofSeconds(1)
        );
        var properties = new AppProperties(
                new AppProperties.Cors(List.of("http://localhost:3000")),
                new AppProperties.Data("mongodb://localhost:27017/evangelizae-test", "redis://localhost:6379"),
                new AppProperties.Liturgy(
                        providerProperties,
                        new AppProperties.Liturgy.Import("test-import-token-with-at-least-32-chars-1234567890")),
                new AppProperties.RateLimit(
                        "test-rate-limit-secret-with-at-least-32-chars-12345678",
                        false,
                        new AppProperties.RateLimit.Policy(30, 1, java.time.Duration.ofSeconds(1)),
                        new AppProperties.RateLimit.Policy(5, 1, java.time.Duration.ofMinutes(1))),
                new AppProperties.Server("framework")
        );
        return new LiturgyService(provider, CLOCK, properties);
    }

    private ProviderLiturgy validLiturgy(java.time.LocalDate date) {
        return new ProviderLiturgy(
                date,
                "Segunda-feira da 21a semana do Tempo Comum",
                LiturgicalColor.GREEN,
                new LiturgyPrayers(null, null, null),
                List.of(new LiturgyGroup(
                        ReadingKind.GOSPEL,
                        List.of(new LiturgyReading(
                                "Proclamacao do Evangelho",
                                "Mt 23,13-22",
                                "Texto fornecido apenas pelo fixture de teste.",
                                null
                        ))
                ))
        );
    }

    private final class SwitchingProvider implements LiturgyProvider {
        private boolean fail;

        @Override
        public ProviderLiturgy fetch(java.time.LocalDate date, String locale) {
            if (fail) {
                throw new ProviderUnavailableException("provider unavailable");
            }
            return validLiturgy(date);
        }
    }
}


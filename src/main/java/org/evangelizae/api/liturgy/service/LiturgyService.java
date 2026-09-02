package org.evangelizae.api.liturgy.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.evangelizae.api.config.AppProperties;
import org.evangelizae.api.liturgy.model.DailyLiturgy;
import org.evangelizae.api.liturgy.model.LiturgyGroup;
import org.evangelizae.api.liturgy.model.LiturgyReading;
import org.evangelizae.api.liturgy.model.LiturgySource;
import org.evangelizae.api.liturgy.provider.LiturgyProvider;
import org.evangelizae.api.liturgy.provider.ProviderLiturgy;
import org.evangelizae.api.liturgy.provider.ProviderUnavailableException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LiturgyService {
    public static final String SUPPORTED_LOCALE = "pt-BR";
    private final LiturgyProvider provider;
    private final Clock clock;
    private final String providerName;
    private final Map<CacheKey, DailyLiturgy> cache = new ConcurrentHashMap<>();

    public LiturgyService(LiturgyProvider provider, Clock clock, AppProperties properties) {
        this.provider = provider;
        this.clock = clock;
        this.providerName = properties.liturgy().provider().name();
    }

    public DailyLiturgy getToday(String timezone, String locale) {
        var zone = parseZone(timezone);
        validateLocale(locale);
        var date = LocalDate.ofInstant(clock.instant(), zone);
        var key = new CacheKey(date, locale);
        try {
            var payload = provider.fetch(date, locale);
            validateProviderResponse(payload, date);
            var live = new DailyLiturgy(payload.date(), payload.title(), payload.color(), payload.prayers(),
                    List.copyOf(payload.groups()), new LiturgySource(providerName, Instant.now(clock),
                    LiturgySource.Freshness.LIVE));
            cache.put(key, live);
            return live;
        } catch (ProviderUnavailableException | InvalidProviderResponseException exception) {
            var cached = cache.get(key);
            if (cached != null && cached.date().equals(date)) {
                return new DailyLiturgy(cached.date(), cached.title(), cached.color(), cached.prayers(),
                        cached.groups(), new LiturgySource(cached.source().provider(), cached.source().fetchedAt(),
                        LiturgySource.Freshness.CACHED));
            }
            throw new LiturgyUnavailableException(
                    "Today's liturgy is unavailable from both the provider and the same-date cache", exception);
        }
    }

    private ZoneId parseZone(String timezone) {
        if (!StringUtils.hasText(timezone)) throw new InvalidLiturgyRequestException("timezone is required");
        try { return ZoneId.of(timezone); }
        catch (ZoneRulesException exception) {
            throw new InvalidLiturgyRequestException("timezone must be a valid IANA timezone", exception);
        }
    }

    private void validateLocale(String locale) {
        if (!SUPPORTED_LOCALE.equals(locale)) throw new InvalidLiturgyRequestException("locale must be pt-BR");
    }

    private void validateProviderResponse(ProviderLiturgy payload, LocalDate expectedDate) {
        if (payload == null || !expectedDate.equals(payload.date()))
            throw new InvalidProviderResponseException("Provider response date does not match the requested date");
        if (!StringUtils.hasText(payload.title()) || payload.color() == null || payload.prayers() == null)
            throw new InvalidProviderResponseException("Provider response metadata is incomplete");
        if (payload.groups() == null || payload.groups().isEmpty())
            throw new InvalidProviderResponseException("Provider response has no reading groups");
        for (LiturgyGroup group : payload.groups()) {
            if (group == null || group.kind() == null || group.items() == null || group.items().isEmpty())
                throw new InvalidProviderResponseException("Provider response contains an invalid reading group");
            for (LiturgyReading reading : group.items()) {
                if (reading == null || !StringUtils.hasText(reading.title()) || !StringUtils.hasText(reading.text()))
                    throw new InvalidProviderResponseException("Provider response contains an invalid reading");
            }
        }
    }

    private record CacheKey(LocalDate date, String locale) {}
}

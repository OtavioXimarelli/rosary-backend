package org.evangelizae.api.config;

import java.time.Duration;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        @Valid @NotNull Cors cors,
        @Valid @NotNull Data data,
        @Valid @NotNull Liturgy liturgy,
        @Valid @NotNull RateLimit rateLimit,
        @Valid @NotNull Server server
) {
    public record Cors(@NotEmpty List<@NotBlank String> allowedOrigins) {}

    public record Data(
            @NotBlank String mongodbUri,
            @NotBlank String redisUrl) {}

    public record Liturgy(@Valid @NotNull Provider provider, @Valid @NotNull Import importConfig) {
        public record Provider(
                boolean enabled,
                String baseUrl,
                @NotBlank String name,
                @NotNull Duration connectTimeout,
                @NotNull Duration readTimeout) {}
        public record Import(
                @NotBlank @Size(min = 32, message = "import token must be at least 32 characters") String token) {}
    }

    public record RateLimit(
            @NotBlank @Size(min = 32, message = "rate-limit key secret must be at least 32 characters") String keySecret,
            boolean trustForwardedHeaders,
            @Valid @NotNull Policy liturgy,
            @Valid @NotNull Policy internalImport) {
        public record Policy(
                @Min(1) int capacity,
                @Min(1) int refillTokens,
                @NotNull Duration refillPeriod) {}
    }

    public record Server(
            @NotBlank String forwardHeadersStrategy) {}
}

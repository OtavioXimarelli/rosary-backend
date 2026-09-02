package org.evangelizae.api.liturgy.provider;

import java.time.LocalDate;

import org.evangelizae.api.config.AppProperties;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class HttpLiturgyProvider implements LiturgyProvider {
    private final AppProperties.Liturgy.Provider properties;
    private final RestClient restClient;

    public HttpLiturgyProvider(AppProperties properties) {
        this.properties = properties.liturgy().provider();
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(this.properties.connectTimeout());
        requestFactory.setReadTimeout(this.properties.readTimeout());
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    @Override
    public ProviderLiturgy fetch(LocalDate date, String locale) {
        if (!properties.enabled() || !StringUtils.hasText(properties.baseUrl())) {
            throw new ProviderUnavailableException("The liturgy provider is not configured");
        }
        var uri = UriComponentsBuilder.fromUriString(properties.baseUrl())
                .queryParam("date", date).queryParam("locale", locale).build().toUri();
        try {
            var payload = restClient.get().uri(uri).retrieve().body(ProviderLiturgy.class);
            if (payload == null) throw new ProviderUnavailableException("The liturgy provider returned an empty response");
            return payload;
        } catch (RestClientException exception) {
            throw new ProviderUnavailableException("The liturgy provider request failed", exception);
        }
    }
}

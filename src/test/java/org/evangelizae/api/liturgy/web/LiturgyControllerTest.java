package org.evangelizae.api.liturgy.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.evangelizae.api.EvangelizaeApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = EvangelizaeApiApplication.class)
@AutoConfigureMockMvc
class LiturgyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsServiceUnavailableWhenNoProviderOrSameDateCacheExists() throws Exception {
        mockMvc.perform(get("/api/v1/liturgy/today")
                        .queryParam("timezone", "America/Sao_Paulo")
                        .queryParam("locale", "pt-BR"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("LITURGY_UNAVAILABLE"))
                .andExpect(jsonPath("$.path").value("/api/v1/liturgy/today"));
    }

    @Test
    void validatesTheLocale() throws Exception {
        mockMvc.perform(get("/api/v1/liturgy/today")
                        .queryParam("timezone", "America/Sao_Paulo")
                        .queryParam("locale", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void healthEndpointReturnsUp() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void actuatorIsNotExposedUnderApiPrefix() throws Exception {
        mockMvc.perform(get("/api/v1/actuator/health"))
                .andExpect(status().isNotFound());
    }
}


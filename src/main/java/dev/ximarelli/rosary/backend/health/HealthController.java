package dev.ximarelli.rosary.backend.health;

import java.time.Instant;
import java.util.Map;

import java.lang.management.ManagementFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> check() {
        return Map.of(
                "status", "ok",
                "timestamp", Instant.now().toString(),
                "uptimeMs", ManagementFactory.getRuntimeMXBean().getUptime());
    }
}

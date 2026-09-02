package org.evangelizae.api;

import org.evangelizae.api.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class EvangelizaeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvangelizaeApiApplication.class, args);
    }
}

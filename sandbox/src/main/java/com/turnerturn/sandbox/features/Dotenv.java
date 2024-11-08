package com.turnerturn.sandbox.features;

import java.nio.file.Paths;
import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(prefix = "features.Dotenv", name = "enabled", havingValue = "true", matchIfMissing = false)
public class Dotenv {

    @PostConstruct
    @Scheduled(fixedRate = 5000)
    protected io.github.cdimascio.dotenv.Dotenv  dotenv() {
        log.debug("Loading .env properties from {}", Paths.get(".").toAbsolutePath().normalize().toString());
        return io.github.cdimascio.dotenv.Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
    }

    public String get(String key) {
        Objects.requireNonNull(key, "Key must not be null");
        return dotenv().get(key);
    }

    public String get(String key, String defaultValue) {
        Objects.requireNonNull(key, "Key must not be null");
        return dotenv().get(key, defaultValue);
    }
}
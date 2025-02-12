package com.localhost.anonymouschat.services.impl;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PresenceService {

    private final Map<String, Instant> userActivity = new ConcurrentHashMap<>();

    public void updateUserPresence(String userId) {
        userActivity.put(userId, Instant.now());
    }

    public Set<String> getActiveUsers() {
        Instant threshold = Instant.now().minus(2, ChronoUnit.MINUTES);
        return userActivity.entrySet().stream()
                .filter(entry -> entry.getValue().isAfter(threshold))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
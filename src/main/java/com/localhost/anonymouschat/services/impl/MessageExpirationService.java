package com.localhost.anonymouschat.services.impl;

import com.localhost.anonymouschat.models.ExpiringMessage;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class MessageExpirationService {

    private final Map<String, ExpiringMessage> expiringMessages = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void scheduleMessageExpiration(ExpiringMessage message) {
        if (message.getExpirationTime() == null) {
            return;
        }

        expiringMessages.put(message.getId(), message);
        
        // Calculate delay until expiration
        Instant now = Instant.now();
        Instant expiration = message.getExpirationTime();
        long delaySeconds = Math.max(1, expiration.getEpochSecond() - now.getEpochSecond());
        
        // Schedule task to remove message when expired
        scheduler.schedule(() -> {
            expiringMessages.remove(message.getId());
            // Additional logic could be added here to notify clients about expiration
        }, delaySeconds, TimeUnit.SECONDS);
    }
    
    public Optional<ExpiringMessage> getMessageIfNotExpired(String messageId) {
        ExpiringMessage message = expiringMessages.get(messageId);
        if (message != null && !message.isExpired()) {
            return Optional.of(message);
        }
        return Optional.empty();
    }
    
    public Map<String, Long> getRemainingTimes() {
        Map<String, Long> remainingTimes = new HashMap<>();
        expiringMessages.forEach((id, message) -> {
            if (!message.isExpired()) {
                remainingTimes.put(id, message.getRemainingTimeSeconds());
            }
        });
        return remainingTimes;
    }
}

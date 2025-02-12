package com.localhost.anonymouschat.services.impl;

import com.localhost.anonymouschat.events.ActiveUsersUpdatedEvent;
import com.localhost.anonymouschat.events.UserDisconnectedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveUserService {

    private final Map<String, UserPresence> activeUsers = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public ActiveUserService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedRate = 5000)
    public void broadcastActiveUsers() {
        eventPublisher.publishEvent(new ActiveUsersUpdatedEvent(getActiveUsers()));
    }

    public void trackSubscription(String username, String destination) {
        activeUsers.compute(username, (k, v) -> {
            if (v == null) {
                v = new UserPresence(username);
            }
            v.addSubscription(destination);
            return v;
        });
        eventPublisher.publishEvent(new ActiveUsersUpdatedEvent(getActiveUsers()));
    }

    public void removeUser(String username) {
        activeUsers.remove(username);
        eventPublisher.publishEvent(new UserDisconnectedEvent(username));
        eventPublisher.publishEvent(new ActiveUsersUpdatedEvent(getActiveUsers()));
    }

    public List<UserPresence> getActiveUsers() {
        return new ArrayList<>(activeUsers.values());
    }

    @Data
    @AllArgsConstructor
    public static class UserPresence {
        private String username;
        private Set<String> subscriptions;
        private Instant lastSeen;

        public UserPresence(String username) {
            this.username = username;
            this.subscriptions = new HashSet<>();
            this.lastSeen = Instant.now();
        }

        public void addSubscription(String destination) {
            subscriptions.add(destination);
            lastSeen = Instant.now();
        }
    }
}
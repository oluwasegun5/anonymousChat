package com.localhost.anonymouschat.events;

import com.localhost.anonymouschat.services.impl.ActiveUserService.UserPresence;
import java.util.List;

public class ActiveUsersUpdatedEvent {
    private final List<UserPresence> activeUsers;

    public ActiveUsersUpdatedEvent(List<UserPresence> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public List<UserPresence> getActiveUsers() {
        return activeUsers;
    }
}
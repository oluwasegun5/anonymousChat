package com.localhost.anonymouschat.events;

import lombok.Getter;

@Getter
public class UserDisconnectedEvent {
    private final String username;

    public UserDisconnectedEvent(String username) {
        this.username = username;
    }

}
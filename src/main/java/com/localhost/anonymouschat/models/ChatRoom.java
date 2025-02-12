package com.localhost.anonymouschat.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document("chat_rooms")
@Data
@RequiredArgsConstructor
public class ChatRoom {
    @Id
    private String id;
    private String name;
    private Set<String> participantIds = new HashSet<>();
    private Set<String> adminIds = new HashSet<>();
    private List<Message> messages = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt = Instant.now();
    private boolean isGroup;
}
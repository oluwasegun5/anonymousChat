package com.localhost.anonymouschat.models;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class ChatMessage {
    private String sender; // Random username
    private String content; // Message content
    private String timestamp;
    private String roomId;
    private String senderId;
}

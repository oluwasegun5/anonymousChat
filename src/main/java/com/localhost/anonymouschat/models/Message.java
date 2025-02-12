package com.localhost.anonymouschat.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message {
    public enum MessageType { TEXT, FILE, IMAGE, SYSTEM }

    private String id;
    private String content;
    private Instant timestamp;
    private MessageType type;
    private String fileUrl;
    private String fileName;
    private String senderId;
}

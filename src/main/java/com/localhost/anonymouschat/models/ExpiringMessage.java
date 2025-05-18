package com.localhost.anonymouschat.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class ExpiringMessage {
    
    @Id
    private String id;
    
    private String content;
    private String senderId;
    
    @Builder.Default
    private Instant timestamp = Instant.now();
    
    private Instant expirationTime;
    private String chatRoomId;
    
    @Builder.Default
    private boolean useDefaultExpiration = false;
    
    @Builder.Default
    private long defaultExpirationHours = 24;
    
    public boolean isExpired() {
        return Instant.now().isAfter(getExpirationTime());
    }
    
    public Instant getExpirationTime() {
        if (expirationTime != null) {
            return expirationTime;
        } else if (useDefaultExpiration && timestamp != null) {
            return timestamp.plus(defaultExpirationHours, ChronoUnit.HOURS);
        }
        return null;
    }
    
    public long getRemainingTimeSeconds() {
        if (isExpired()) {
            return 0;
        }
        
        Instant now = Instant.now();
        Instant expiration = getExpirationTime();
        
        if (expiration == null) {
            return Long.MAX_VALUE; // No expiration
        }
        
        return ChronoUnit.SECONDS.between(now, expiration);
    }
}

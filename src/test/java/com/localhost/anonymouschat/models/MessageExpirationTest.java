package com.localhost.anonymouschat.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

class MessageExpirationTest {

    @Test
    void testCreateMessageWithExpiration() {
        Instant expirationTime = Instant.now().plus(1, ChronoUnit.HOURS);
        
        ExpiringMessage message = new ExpiringMessage();
        message.setContent("This message will self-destruct");
        message.setSenderId("user123");
        message.setExpirationTime(expirationTime);
        
        assertEquals("This message will self-destruct", message.getContent());
        assertEquals("user123", message.getSenderId());
        assertEquals(expirationTime, message.getExpirationTime());
        assertFalse(message.isExpired());
    }
    
    @Test
    void testMessageExpiration() {
        // Create a message that expired 5 minutes ago
        Instant expirationTime = Instant.now().minus(5, ChronoUnit.MINUTES);
        
        ExpiringMessage message = ExpiringMessage.builder()
                .content("This message has expired")
                .senderId("user456")
                .expirationTime(expirationTime)
                .build();
        
        assertTrue(message.isExpired());
    }
    
    @Test
    void testMessageWithDefaultExpiration() {
        ExpiringMessage message = ExpiringMessage.builder()
                .content("This message has default expiration")
                .senderId("user789")
                .useDefaultExpiration(true)
                .build();
        
        // Default expiration should be 24 hours from creation
        Instant expectedExpiration = message.getTimestamp().plus(24, ChronoUnit.HOURS);
        assertEquals(expectedExpiration, message.getExpirationTime());
    }
    
    @Test
    void testRemainingTimeCalculation() {
        Instant expirationTime = Instant.now().plus(30, ChronoUnit.MINUTES);
        
        ExpiringMessage message = ExpiringMessage.builder()
                .content("Check remaining time")
                .senderId("user101")
                .expirationTime(expirationTime)
                .build();
        
        // Remaining time should be approximately 30 minutes (in seconds)
        long expectedSeconds = 30 * 60;
        long actualSeconds = message.getRemainingTimeSeconds();
        
        // Allow for a small difference due to test execution time
        assertTrue(Math.abs(expectedSeconds - actualSeconds) < 10);
    }
}

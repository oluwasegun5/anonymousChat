package com.localhost.anonymouschat.services.impl;

import com.localhost.anonymouschat.models.Message;
import com.localhost.anonymouschat.models.User;
import com.localhost.anonymouschat.models.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReadReceiptServiceTest {

    private ReadReceiptService readReceiptService;
    
    @Mock
    private UserProfileService userProfileService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        readReceiptService = new ReadReceiptService(userProfileService);
    }
    
    @Test
    void testMarkMessageAsRead() {
        String messageId = "msg123";
        String userId = "user456";
        Instant readTime = Instant.now();
        
        readReceiptService.markMessageAsRead(messageId, userId, readTime);
        
        assertTrue(readReceiptService.hasUserReadMessage(messageId, userId));
        assertEquals(readTime, readReceiptService.getReadTime(messageId, userId));
    }
    
    @Test
    void testGetReadReceipts() {
        String messageId = "msg123";
        String user1 = "user1";
        String user2 = "user2";
        Instant readTime1 = Instant.now().minusSeconds(60);
        Instant readTime2 = Instant.now();
        
        readReceiptService.markMessageAsRead(messageId, user1, readTime1);
        readReceiptService.markMessageAsRead(messageId, user2, readTime2);
        
        Map<String, Instant> readReceipts = readReceiptService.getReadReceipts(messageId);
        
        assertEquals(2, readReceipts.size());
        assertEquals(readTime1, readReceipts.get(user1));
        assertEquals(readTime2, readReceipts.get(user2));
    }
    
    @Test
    void testGetUnreadMessages() {
        String chatRoomId = "room123";
        String userId = "user456";
        List<Message> messages = List.of(
            Message.builder().id("msg1").build(),
            Message.builder().id("msg2").build(),
            Message.builder().id("msg3").build()
        );
        
        // Mark one message as read
        readReceiptService.markMessageAsRead("msg1", userId, Instant.now());
        
        List<Message> unreadMessages = readReceiptService.getUnreadMessages(chatRoomId, userId, messages);
        
        assertEquals(2, unreadMessages.size());
        assertFalse(unreadMessages.stream().anyMatch(m -> m.getId().equals("msg1")));
        assertTrue(unreadMessages.stream().anyMatch(m -> m.getId().equals("msg2")));
        assertTrue(unreadMessages.stream().anyMatch(m -> m.getId().equals("msg3")));
    }
    
    @Test
    void testRespectUserPrivacySettings() {
        String messageId = "msg123";
        String senderId = "sender456";
        String readerId = "reader789";
        
        // Setup user who doesn't want to share read receipts
        UserProfile profile = UserProfile.builder()
            .userId(readerId)
            .showReadReceipts(false)
            .build();
        
        when(userProfileService.getUserProfile(readerId)).thenReturn(profile);
        
        // Mark message as read
        readReceiptService.markMessageAsRead(messageId, readerId, Instant.now());
        
        // Should respect privacy settings
        assertFalse(readReceiptService.isReadReceiptVisible(messageId, readerId, senderId));
        
        // Change setting to allow read receipts
        profile.setShowReadReceipts(true);
        assertTrue(readReceiptService.isReadReceiptVisible(messageId, readerId, senderId));
    }
}

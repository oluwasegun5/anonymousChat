package com.localhost.anonymouschat.services.impl;

import com.localhost.anonymouschat.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserBlockingServiceTest {

    private UserBlockingService userBlockingService;
    
    @Mock
    private UserDetailsService userDetailsService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userBlockingService = new UserBlockingService(userDetailsService);
    }
    
    @Test
    void testBlockUser() {
        String userId = "user123";
        String blockedUserId = "blockedUser456";
        
        userBlockingService.blockUser(userId, blockedUserId);
        
        assertTrue(userBlockingService.isUserBlocked(userId, blockedUserId));
        assertEquals(1, userBlockingService.getBlockedUsers(userId).size());
        assertTrue(userBlockingService.getBlockedUsers(userId).contains(blockedUserId));
    }
    
    @Test
    void testUnblockUser() {
        String userId = "user123";
        String blockedUserId = "blockedUser456";
        
        // First block the user
        userBlockingService.blockUser(userId, blockedUserId);
        assertTrue(userBlockingService.isUserBlocked(userId, blockedUserId));
        
        // Then unblock
        userBlockingService.unblockUser(userId, blockedUserId);
        
        assertFalse(userBlockingService.isUserBlocked(userId, blockedUserId));
        assertEquals(0, userBlockingService.getBlockedUsers(userId).size());
    }
    
    @Test
    void testGetBlockedUsers() {
        String userId = "user123";
        String blockedUser1 = "blocked1";
        String blockedUser2 = "blocked2";
        
        userBlockingService.blockUser(userId, blockedUser1);
        userBlockingService.blockUser(userId, blockedUser2);
        
        Set<String> blockedUsers = userBlockingService.getBlockedUsers(userId);
        
        assertEquals(2, blockedUsers.size());
        assertTrue(blockedUsers.contains(blockedUser1));
        assertTrue(blockedUsers.contains(blockedUser2));
    }
    
    @Test
    void testCanSendMessage() {
        String sender = "user123";
        String recipient = "user456";
        String blockedUser = "blockedUser789";
        
        userBlockingService.blockUser(sender, blockedUser);
        
        assertTrue(userBlockingService.canSendMessage(sender, recipient));
        assertFalse(userBlockingService.canSendMessage(blockedUser, sender));
    }
}

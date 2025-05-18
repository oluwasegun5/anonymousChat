package com.localhost.anonymouschat.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    @Test
    void testCreateUserProfile() {
        UserProfile profile = new UserProfile();
        profile.setDisplayName("Anonymous Fox");
        profile.setAvatarUrl("default_avatar.png");
        profile.setStatusMessage("Available");
        
        assertEquals("Anonymous Fox", profile.getDisplayName());
        assertEquals("default_avatar.png", profile.getAvatarUrl());
        assertEquals("Available", profile.getStatusMessage());
    }
    
    @Test
    void testUserProfileBuilder() {
        UserProfile profile = UserProfile.builder()
                .displayName("Anonymous Wolf")
                .avatarUrl("wolf_avatar.png")
                .statusMessage("Busy")
                .build();
        
        assertEquals("Anonymous Wolf", profile.getDisplayName());
        assertEquals("wolf_avatar.png", profile.getAvatarUrl());
        assertEquals("Busy", profile.getStatusMessage());
    }
    
    @Test
    void testUserProfilePrivacySettings() {
        UserProfile profile = UserProfile.builder()
                .displayName("Private User")
                .showStatusToContacts(false)
                .showReadReceipts(false)
                .build();
        
        assertFalse(profile.isShowStatusToContacts());
        assertFalse(profile.isShowReadReceipts());
    }
}

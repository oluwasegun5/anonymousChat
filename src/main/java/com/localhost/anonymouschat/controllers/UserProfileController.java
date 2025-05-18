package com.localhost.anonymouschat.controllers;

import com.localhost.anonymouschat.models.UserProfile;
import com.localhost.anonymouschat.services.impl.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.localhost.anonymouschat.models.User;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<UserProfile> getUserProfile(@AuthenticationPrincipal User user) {
        UserProfile profile = userProfileService.getUserProfile(user.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<UserProfile> updateUserProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UserProfile updatedProfile) {
        UserProfile savedProfile = userProfileService.updateUserProfile(user.getId(), updatedProfile);
        return ResponseEntity.ok(savedProfile);
    }

    @PatchMapping("/privacy")
    public ResponseEntity<UserProfile> updatePrivacySettings(
            @AuthenticationPrincipal User user,
            @RequestBody UserProfile privacySettings) {
        
        UserProfile currentProfile = userProfileService.getUserProfile(user.getId());
        currentProfile.setShowReadReceipts(privacySettings.isShowReadReceipts());
        currentProfile.setShowStatusToContacts(privacySettings.isShowStatusToContacts());
        currentProfile.setAllowDirectMessages(privacySettings.isAllowDirectMessages());
        currentProfile.setNotificationsEnabled(privacySettings.isNotificationsEnabled());
        
        UserProfile savedProfile = userProfileService.saveUserProfile(currentProfile);
        return ResponseEntity.ok(savedProfile);
    }
}

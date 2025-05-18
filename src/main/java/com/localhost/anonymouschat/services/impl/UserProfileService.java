package com.localhost.anonymouschat.services.impl;

import com.localhost.anonymouschat.models.UserProfile;
import com.localhost.anonymouschat.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile getUserProfile(String userId) {
        return userProfileRepository.findByUserId(userId)
                .orElse(UserProfile.builder()
                        .userId(userId)
                        .displayName("Anonymous User")
                        .avatarUrl("default_avatar.png")
                        .build());
    }

    public UserProfile saveUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    public UserProfile updateUserProfile(String userId, UserProfile updatedProfile) {
        UserProfile existingProfile = getUserProfile(userId);
        
        if (updatedProfile.getDisplayName() != null) {
            existingProfile.setDisplayName(updatedProfile.getDisplayName());
        }
        
        if (updatedProfile.getAvatarUrl() != null) {
            existingProfile.setAvatarUrl(updatedProfile.getAvatarUrl());
        }
        
        if (updatedProfile.getStatusMessage() != null) {
            existingProfile.setStatusMessage(updatedProfile.getStatusMessage());
        }
        
        existingProfile.setShowStatusToContacts(updatedProfile.isShowStatusToContacts());
        existingProfile.setShowReadReceipts(updatedProfile.isShowReadReceipts());
        existingProfile.setAllowDirectMessages(updatedProfile.isAllowDirectMessages());
        existingProfile.setNotificationsEnabled(updatedProfile.isNotificationsEnabled());
        
        return userProfileRepository.save(existingProfile);
    }
}

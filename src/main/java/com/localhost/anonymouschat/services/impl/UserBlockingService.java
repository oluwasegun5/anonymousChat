package com.localhost.anonymouschat.services.impl;

import com.localhost.anonymouschat.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserBlockingService {

    private final UserDetailsService userDetailsService;
    private final Map<String, Set<String>> blockedUsersMap = new ConcurrentHashMap<>();

    public UserBlockingService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void blockUser(String userId, String blockedUserId) {
        blockedUsersMap.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                .add(blockedUserId);
    }

    public void unblockUser(String userId, String blockedUserId) {
        Set<String> blockedUsers = blockedUsersMap.get(userId);
        if (blockedUsers != null) {
            blockedUsers.remove(blockedUserId);
        }
    }

    public boolean isUserBlocked(String userId, String potentiallyBlockedUserId) {
        Set<String> blockedUsers = blockedUsersMap.get(userId);
        return blockedUsers != null && blockedUsers.contains(potentiallyBlockedUserId);
    }

    public Set<String> getBlockedUsers(String userId) {
        return blockedUsersMap.getOrDefault(userId, Collections.emptySet());
    }

    public boolean canSendMessage(String senderId, String recipientId) {
        // Check if recipient has blocked sender
        return !isUserBlocked(recipientId, senderId);
    }
}

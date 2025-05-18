package com.localhost.anonymouschat.services.impl;

import com.localhost.anonymouschat.models.Message;
import com.localhost.anonymouschat.models.UserProfile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ReadReceiptService {

    private final UserProfileService userProfileService;
    
    // Map of messageId -> Map of userId -> readTime
    private final Map<String, Map<String, Instant>> readReceiptsMap = new ConcurrentHashMap<>();

    public ReadReceiptService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    public void markMessageAsRead(String messageId, String userId, Instant readTime) {
        readReceiptsMap.computeIfAbsent(messageId, k -> new ConcurrentHashMap<>())
                .put(userId, readTime);
    }

    public boolean hasUserReadMessage(String messageId, String userId) {
        Map<String, Instant> messageReaders = readReceiptsMap.get(messageId);
        return messageReaders != null && messageReaders.containsKey(userId);
    }

    public Instant getReadTime(String messageId, String userId) {
        Map<String, Instant> messageReaders = readReceiptsMap.get(messageId);
        if (messageReaders != null) {
            return messageReaders.get(userId);
        }
        return null;
    }

    public Map<String, Instant> getReadReceipts(String messageId) {
        return readReceiptsMap.getOrDefault(messageId, new HashMap<>());
    }

    public List<Message> getUnreadMessages(String chatRoomId, String userId, List<Message> messages) {
        return messages.stream()
                .filter(message -> !hasUserReadMessage(message.getId(), userId))
                .collect(Collectors.toList());
    }

    public boolean isReadReceiptVisible(String messageId, String readerId, String viewerId) {
        UserProfile readerProfile = userProfileService.getUserProfile(readerId);
        return readerProfile != null && readerProfile.isShowReadReceipts();
    }
}

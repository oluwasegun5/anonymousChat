package com.localhost.anonymouschat.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_profiles")
public class UserProfile {
    
    @Id
    private String id;
    
    private String userId;
    private String displayName;
    private String avatarUrl;
    private String statusMessage;
    
    @Builder.Default
    private boolean showStatusToContacts = true;
    
    @Builder.Default
    private boolean showReadReceipts = true;
    
    @Builder.Default
    private boolean allowDirectMessages = true;
    
    @Builder.Default
    private boolean notificationsEnabled = true;
}

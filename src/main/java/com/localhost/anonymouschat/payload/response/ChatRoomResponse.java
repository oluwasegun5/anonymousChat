package com.localhost.anonymouschat.payload.response;

import com.localhost.anonymouschat.models.Message;
import lombok.*;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatRoomResponse {
    private String id;
    private String name;          // Add this
    private boolean isGroup;      // Add this
    private List<UserDetailResponse> participants;  // Changed from 'me' and 'you'
    private Message lastMessage;  // Add this
    private Instant createdAt;

}

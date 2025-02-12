package com.localhost.anonymouschat.services.inter;

import com.localhost.anonymouschat.models.ChatRoom;
import com.localhost.anonymouschat.models.Message;
import com.localhost.anonymouschat.models.User;
import com.localhost.anonymouschat.payload.response.ChatRoomResponse;

import java.util.List;
import java.util.Set;

public interface ChatService {
    ChatRoom createGroupChat(User creator, String chatName, Set<String> participantIds);
    void addParticipants(String chatRoomId, Set<String> participantIds);
    void removeParticipant(String chatRoomId, String participantId);
    ChatRoom getChatRoom(String roomId);
    List<ChatRoomResponse> getUserChatRooms(User user);
    void addMessage(String chatRoomId, Message message);
}

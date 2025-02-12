package com.localhost.anonymouschat.services.impl;

import com.localhost.anonymouschat.exception.ResourceNotFoundException;
import com.localhost.anonymouschat.models.ChatRoom;
import com.localhost.anonymouschat.models.Message;
import com.localhost.anonymouschat.models.User;
import com.localhost.anonymouschat.payload.response.ChatRoomResponse;
import com.localhost.anonymouschat.repositories.ChatRoomRepository;
import com.localhost.anonymouschat.repositories.UserRepository;
import com.localhost.anonymouschat.services.inter.ChatService;
import com.localhost.anonymouschat.services.inter.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MessageModerationService moderationService;
    private final EncryptionService encryptionService;

    @Override
    public ChatRoom createGroupChat(User creator, String chatName, Set<String> participantIds) {
        validateNewGroupParticipants(creator.getId(), participantIds);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(chatName);
        chatRoom.setGroup(true);
        chatRoom.getParticipantIds().add(creator.getId());
        chatRoom.getParticipantIds().addAll(participantIds);
        chatRoom.getAdminIds().add(creator.getId());

        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public void addParticipants(String chatRoomId, Set<String> participantIds) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);
        validateExistingChatParticipants(chatRoom.getParticipantIds(), participantIds);

        Set<String> newParticipants = new HashSet<>(participantIds);
        newParticipants.removeAll(chatRoom.getParticipantIds());

        chatRoom.getParticipantIds().addAll(newParticipants);
        chatRoomRepository.save(chatRoom);
    }

    @Override
    public void removeParticipant(String chatRoomId, String participantId) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);

        if (!chatRoom.getParticipantIds().contains(participantId)) {
            throw new IllegalArgumentException("User not in chat room");
        }

        chatRoom.getParticipantIds().remove(participantId);
        chatRoom.getAdminIds().remove(participantId);
        chatRoomRepository.save(chatRoom);
    }

    @Override
    public ChatRoom getChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));
    }

    @Override
    public List<ChatRoomResponse> getUserChatRooms(User user) {
        return chatRoomRepository.findByParticipantIdsContains(user.getId())
                .stream()
                .map(this::mapToChatRoomResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void addMessage(String chatRoomId, Message message) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);
        validateParticipant(chatRoom, message.getSenderId());

        Message moderatedMessage = moderationService.moderateMessage(message);
        moderatedMessage.setContent(encryptionService.encrypt(moderatedMessage.getContent()));

        chatRoom.getMessages().add(moderatedMessage);
        chatRoomRepository.save(chatRoom);
    }

    private ChatRoomResponse mapToChatRoomResponse(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .isGroup(chatRoom.isGroup())
                .participants(userService.getUsersDetails(chatRoom.getParticipantIds()))
                .lastMessage(chatRoom.getMessages().isEmpty() ?
                        null : chatRoom.getMessages().get(chatRoom.getMessages().size() - 1))
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    private void validateNewGroupParticipants(String creatorId, Set<String> participantIds) {
        Set<String> allIds = new HashSet<>(participantIds);
        allIds.add(creatorId);
        validateUserExistence(allIds);
    }

    private void validateExistingChatParticipants(Set<String> existingParticipants, Set<String> newParticipants) {
        validateUserExistence(newParticipants);

        Set<String> duplicates = new HashSet<>(newParticipants);
        duplicates.retainAll(existingParticipants);
        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException("Users already in chat: " + duplicates);
        }
    }

    private void validateParticipants(Set<String> existingParticipants, Set<String> newParticipants) {
        Set<String> combined = new HashSet<>(existingParticipants);
        combined.addAll(newParticipants);
        validateUserExistence(combined);

        // Check for duplicates
        Set<String> duplicates = new HashSet<>(newParticipants);
        duplicates.retainAll(existingParticipants);
        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException("Users already in chat: " + duplicates);
        }
    }

    private void validateUserExistence(Set<String> userIds) {
        List<String> missingUsers = userIds.stream()
                .filter(id -> !userRepository.existsById(id))
                .collect(Collectors.toList());

        if (!missingUsers.isEmpty()) {
            throw new ResourceNotFoundException("Users not found: " + missingUsers);
        }
    }

    private void validateParticipant(ChatRoom chatRoom, String userId) {
        if (!chatRoom.getParticipantIds().contains(userId)) {
            throw new SecurityException("User not authorized to post in this chat");
        }
    }
}
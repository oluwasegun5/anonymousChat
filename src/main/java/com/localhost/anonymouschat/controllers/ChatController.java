package com.localhost.anonymouschat.controllers;

import com.localhost.anonymouschat.models.ChatRoom;
import com.localhost.anonymouschat.models.Message;
import com.localhost.anonymouschat.models.User;
import com.localhost.anonymouschat.payload.request.CreateGroupRequest;
import com.localhost.anonymouschat.payload.request.MessageRequest;
import com.localhost.anonymouschat.payload.response.ChatRoomResponse;
import com.localhost.anonymouschat.payload.response.MessageResponse;
import com.localhost.anonymouschat.payload.response.UserDetailResponse;
import com.localhost.anonymouschat.services.inter.ChatService;
import com.localhost.anonymouschat.services.inter.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@Validated
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    private final ObjectProvider<SimpMessagingTemplate> messagingTemplateProvider;

    @Autowired
    public ChatController(ObjectProvider<SimpMessagingTemplate> messagingTemplateProvider, ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
        this.messagingTemplateProvider = messagingTemplateProvider;
    }

    @PostMapping("/group")
    public ResponseEntity<ChatRoomResponse> createGroupChat(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateGroupRequest request
    ) {
        ChatRoom chatRoom = chatService.createGroupChat(user, request.getName(), request.getParticipantIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(chatRoom));
    }

    @PostMapping("/{roomId}/messages")
    public ResponseEntity<Message> sendMessage(
            @PathVariable String roomId,
            @AuthenticationPrincipal User sender,
            @RequestBody @Valid MessageRequest request
    ) {
        Message message = Message.builder()
                .senderId(sender.getId())
                .content(request.getContent())
                .timestamp(Instant.now())
                .build();

        chatService.addMessage(roomId, message);

        // Broadcast to WebSocket subscribers
        Objects.requireNonNull(messagingTemplateProvider.getIfAvailable()).convertAndSend(
                "/topic/chat." + roomId + ".messages",
                mapToMessageResponse(message, userService.getUserDetail(sender))
        );

        return ResponseEntity.ok(message);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        ChatRoom chatRoom = chatService.getChatRoom(roomId);
        List<Message> messages = chatRoom.getMessages();

        // Implement pagination logic
        List<MessageResponse> response = messages.stream()
                .skip(page * size)
                .limit(size)
                .map(msg -> mapToMessageResponse(msg, userService.getUserDetail(msg.getSenderId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(@AuthenticationPrincipal User user) {
        List<ChatRoomResponse> chatRooms = chatService.getUserChatRooms(user);
        return ResponseEntity.ok(chatRooms);
    }

    private MessageResponse mapToMessageResponse(Message message, UserDetailResponse sender) {
        return MessageResponse.builder()
                .id(UUID.randomUUID().toString()) // Generate unique ID if needed
                .content(message.getContent())
                .sender(sender)
                .timestamp(Instant.from(message.getTimestamp()))
                .build();
    }

    private ChatRoomResponse mapToResponse(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .isGroup(chatRoom.isGroup())
                .participants(userService.getUsersDetails(chatRoom.getParticipantIds()))
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}
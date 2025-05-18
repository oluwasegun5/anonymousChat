package com.localhost.anonymouschat.controllers;

import com.localhost.anonymouschat.models.ExpiringMessage;
import com.localhost.anonymouschat.services.impl.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.localhost.anonymouschat.models.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final ChatServiceImpl chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/expiring")
    public ResponseEntity<ExpiringMessage> sendExpiringMessage(
            @AuthenticationPrincipal User user,
            @RequestBody ExpiringMessage message,
            @RequestParam(required = false) Long expirationMinutes) {
        
        message.setSenderId(user.getId());
        
        if (expirationMinutes != null) {
            message.setExpirationTime(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES));
        } else if (message.isUseDefaultExpiration()) {
            message.setExpirationTime(Instant.now().plus(message.getDefaultExpirationHours(), ChronoUnit.HOURS));
        }
        
        ExpiringMessage savedMessage = chatService.saveExpiringMessage(message);
        
        // Notify subscribers about the new message
        messagingTemplate.convertAndSend(
                "/topic/chat." + message.getChatRoomId() + ".messages", 
                savedMessage);
        
        return ResponseEntity.ok(savedMessage);
    }
    
    @GetMapping("/expiring/{messageId}/remaining")
    public ResponseEntity<Long> getRemainingTime(@PathVariable String messageId) {
        ExpiringMessage message = chatService.getExpiringMessage(messageId);
        if (message == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(message.getRemainingTimeSeconds());
    }
}

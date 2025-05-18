package com.localhost.anonymouschat.controllers;

import com.localhost.anonymouschat.models.Message;
import com.localhost.anonymouschat.services.impl.ReadReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.localhost.anonymouschat.models.User;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReadReceiptController {

    private final ReadReceiptService readReceiptService;

    @PostMapping("/{messageId}")
    public ResponseEntity<Void> markMessageAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable String messageId) {
        
        readReceiptService.markMessageAsRead(messageId, user.getId(), Instant.now());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<Map<String, Instant>> getReadReceipts(
            @PathVariable String messageId) {
        
        Map<String, Instant> readReceipts = readReceiptService.getReadReceipts(messageId);
        return ResponseEntity.ok(readReceipts);
    }

    @GetMapping("/unread/{chatRoomId}")
    public ResponseEntity<List<Message>> getUnreadMessages(
            @AuthenticationPrincipal User user,
            @PathVariable String chatRoomId,
            @RequestBody List<Message> messages) {
        
        List<Message> unreadMessages = readReceiptService.getUnreadMessages(chatRoomId, user.getId(), messages);
        return ResponseEntity.ok(unreadMessages);
    }
}

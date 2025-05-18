package com.localhost.anonymouschat.controllers;

import com.localhost.anonymouschat.services.impl.UserBlockingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.localhost.anonymouschat.models.User;

import java.util.Set;

@RestController
@RequestMapping("/api/blocking")
@RequiredArgsConstructor
public class UserBlockingController {

    private final UserBlockingService userBlockingService;

    @PostMapping("/{blockedUserId}")
    public ResponseEntity<Void> blockUser(
            @AuthenticationPrincipal User user,
            @PathVariable String blockedUserId) {
        
        userBlockingService.blockUser(user.getId(), blockedUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{blockedUserId}")
    public ResponseEntity<Void> unblockUser(
            @AuthenticationPrincipal User user,
            @PathVariable String blockedUserId) {
        
        userBlockingService.unblockUser(user.getId(), blockedUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Set<String>> getBlockedUsers(@AuthenticationPrincipal User user) {
        Set<String> blockedUsers = userBlockingService.getBlockedUsers(user.getId());
        return ResponseEntity.ok(blockedUsers);
    }
}

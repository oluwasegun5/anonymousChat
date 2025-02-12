package com.localhost.anonymouschat.services.impl;

import com.localhost.anonymouschat.models.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MessageModerationService {
    private final List<Pattern> bannedPatterns = List.of(
            Pattern.compile("badword1", Pattern.CASE_INSENSITIVE),
            Pattern.compile("badword2", Pattern.CASE_INSENSITIVE)
    );

    private final Set<String> blockedUsers = new HashSet<>();

    public Message moderateMessage(Message message) {
        if (blockedUsers.contains(message.getSenderId())) {
            throw new SecurityException("User is blocked from sending messages");
        }

        String moderatedContent = bannedPatterns.stream()
                .reduce(message.getContent(),
                        (content, pattern) -> pattern.matcher(content).replaceAll("***"),
                        (c1, c2) -> c2);

        message.setContent(moderatedContent);
        return message;
    }
}

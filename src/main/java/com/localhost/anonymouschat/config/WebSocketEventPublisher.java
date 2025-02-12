package com.localhost.anonymouschat.config;

import com.localhost.anonymouschat.events.ActiveUsersUpdatedEvent;
import com.localhost.anonymouschat.events.UserDisconnectedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketEventPublisher {

    private SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public WebSocketEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    @Lazy
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleActiveUsersUpdate(ActiveUsersUpdatedEvent event) {
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/presence", event.getActiveUsers());
        }
    }

    @EventListener
    public void handleUserDisconnected(UserDisconnectedEvent event) {
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/disconnections", event.getUsername());
        }
    }
}
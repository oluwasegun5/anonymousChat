package com.localhost.anonymouschat.config;

import com.localhost.anonymouschat.services.impl.ActiveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.ChannelInterceptor;


public class PresenceChannelInterceptor implements ChannelInterceptor {

    private final ActiveUserService activeUserService;
    private SimpUserRegistry userRegistry;

    // Only inject ActiveUserService via constructor
    public PresenceChannelInterceptor(ActiveUserService activeUserService) {
        this.activeUserService = activeUserService;
    }

    @Autowired
    public void setUserRegistry(SimpUserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
            String username = accessor.getUser().getName();
            String destination = accessor.getDestination();
            activeUserService.trackSubscription(username, destination);
        }
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel,
                                    boolean sent, Exception ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.DISCONNECT) {
            String username = accessor.getUser().getName();
            activeUserService.removeUser(username);
        }
    }
}
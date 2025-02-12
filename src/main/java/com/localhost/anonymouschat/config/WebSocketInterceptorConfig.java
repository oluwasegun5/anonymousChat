package com.localhost.anonymouschat.config;

import com.localhost.anonymouschat.services.impl.ActiveUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class WebSocketInterceptorConfig {

    @Bean
    @Lazy
    public PresenceChannelInterceptor presenceChannelInterceptor(ActiveUserService activeUserService) {
        return new PresenceChannelInterceptor(activeUserService);
    }
}
package com.localhost.anonymouschat.config;

import com.localhost.anonymouschat.services.inter.JwtService;
import com.localhost.anonymouschat.services.inter.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PresenceChannelInterceptor presenceChannelInterceptor;

    @Autowired
    public WebSocketConfig(JwtService jwtService,
                           UserDetailsService userDetailsService,
                           @Lazy PresenceChannelInterceptor presenceChannelInterceptor) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.presenceChannelInterceptor = presenceChannelInterceptor;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                authChannelInterceptorAdapter(),
                presenceChannelInterceptor
        );
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:*", "https://*.yourdomain.com")
                .withSockJS()
                .setInterceptors(httpSessionHandshakeInterceptor());
    }

//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableStompBrokerRelay("/topic", "/queue")
//                .setRelayHost("localhost")
//                .setRelayPort(61613)
//                .setClientLogin("guest")
//                .setClientPasscode("guest");
//
//        registry.setApplicationDestinationPrefixes("/app")
//                .setUserDestinationPrefix("/user");
//    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Use simple in-memory broker instead of external STOMP relay
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(5 * 1024 * 1024); // 5MB
        registry.setSendTimeLimit(15 * 1000); // 15 seconds
        registry.setSendBufferSizeLimit(5 * 1024 * 1024); // 5MB
    }

    @Bean
    public ChannelInterceptor authChannelInterceptorAdapter() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        String username = jwtService.extractUserName(token);
                        if (username != null && jwtService.isTokenValid(token, userDetailsService.loadUserByUsername(username))) {
                            accessor.setUser(() -> username);
                        }
                    }
                }
                return message;
            }
        };
    }

    @Bean
    public HandshakeInterceptor httpSessionHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                           WebSocketHandler wsHandler, Map<String, Object> attributes) {
                if (request instanceof ServletServerHttpRequest) {
                    ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                    attributes.put("sessionId", servletRequest.getServletRequest().getSession().getId());
                }
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Exception exception) {
            }
        };
    }
}
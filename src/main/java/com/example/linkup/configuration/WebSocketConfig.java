package com.example.linkup.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");  // Kênh broadcast: nơi tn realtime đc gửi đến
        config.setApplicationDestinationPrefixes("/app"); // Prefix cho client gửi:client gửi lên bằng /app/..
        config.setUserDestinationPrefix("/user");       // Prefix cho user cụ thể: sv gửi tn cho từng user theo /user/{userId}
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")          // Endpoint kết nối WebSocket
                .setAllowedOrigins("http://localhost:3000")
                .setAllowedOriginPatterns("*") // Cho phép mọi origin
                .withSockJS();                // Hỗ trợ SockJS fallback
    }
}

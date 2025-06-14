package com.example.linkup.configuration;

import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic", "/queue");  // Kênh broadcast
//        config.setApplicationDestinationPrefixes("/app"); // Prefix cho client gửi
//        config.setUserDestinationPrefix("/user");       // Prefix cho user cụ thể
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws")          // Endpoint kết nối WebSocket
//                .setAllowedOriginPatterns("*") // Cho phép mọi origin
//                .withSockJS();                // Hỗ trợ SockJS fallback
//    }
}

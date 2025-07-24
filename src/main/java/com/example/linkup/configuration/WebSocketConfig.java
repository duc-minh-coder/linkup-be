package com.example.linkup.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static com.example.linkup.constant.AllowedOrigins.ORIGINS;

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
        registry.addEndpoint("/ws")     // endpoint kết nối ws
                .addInterceptors(new CustomHandshakeInterceptor())
                .setHandshakeHandler(new UserHandshakeHandler())
                .setAllowedOrigins(ORIGINS) // tránh bị cors
                .withSockJS();                // Hỗ trợ SockJS fallback
    }
}

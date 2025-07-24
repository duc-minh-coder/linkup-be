package com.example.linkup.configuration;

import lombok.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.security.Principal;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class UserHandshakeHandler extends DefaultHandshakeHandler {
    protected Principal determineUser(
            @NonNull ServerHttpRequest request,
            @NonNull WebSocketHandler webSocketHandler,
            @NonNull Map<String, Object> attributes
    ) {
        URI uri = request.getURI();
        String query = uri.getQuery(); //sau ? của port
        String senderId;

        if (query != null && query.contains("senderId=")) {
            senderId = Arrays.stream(query.split("&"))
                    .filter(param -> param.startsWith("senderId="))
                    .map(param -> param.split("=")[1])
                    .findFirst()
                    .orElse(null);
        } else {
            senderId = null;
        }

        if (senderId == null) return null;

        return () -> senderId;
    }
}

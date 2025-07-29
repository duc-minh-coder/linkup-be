package com.example.linkup.events;

import com.example.linkup.dto.request.ChatMessage;
import com.example.linkup.enums.MessageType;
import com.example.linkup.service.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Slf4j
@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    private OnlineUserService onlineUserService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("New WebSocket connection established");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String senderId = null;

        if (accessor.getSessionAttributes() != null) {
            senderId = (String) accessor.getSessionAttributes().get("senderId");
        }

        if (senderId != null) {
            log.info("user disconnected: " + senderId);

            onlineUserService.setOfflineUser(Integer.parseInt(senderId));

            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setSenderId(Integer.parseInt(senderId));
            leaveMessage.setType(MessageType.OFFLINE);

            //cho user khác biết user này đã offline
            simpMessageSendingOperations.convertAndSend("/topic/status", leaveMessage);
        }
    }
}

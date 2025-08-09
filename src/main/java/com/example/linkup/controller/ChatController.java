package com.example.linkup.controller;

import com.example.linkup.dto.request.ChatMessage;
import com.example.linkup.dto.response.UnreadMessagesCountResponse;
import com.example.linkup.enums.MessageType;
import com.example.linkup.service.MessageService;
import com.example.linkup.service.OnlineUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    SimpMessagingTemplate messagingTemplate;
    MessageService messageService;
    OnlineUserService onlineUserService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        message.setCreatedTime(new Date());

        messageService.saveMessage(message);

        // gửi đến receiver(client sẽ vào /user/{receiverId}/user/queue/messages
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiverId()),
                "/queue/messages",
                message);

        if (message.getType() == MessageType.ONLINE)
            onlineUserService.setOnlineUser(message.getSenderId());

        long unreadCount = messageService.getUnReadCountBetweenUser(message.getSenderId());

        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiverId()),
                "/queue/unread-count",
                new UnreadMessagesCountResponse(message.getSenderId(), unreadCount)
        );
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload ChatMessage message) {
        message.setType(MessageType.TYPING);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiverId()),
                "/queue/messages",
                message
        );
    }

    @MessageMapping("/chat.stopTyping")
    public void stopTyping(@Payload ChatMessage message) {
        message.setType(MessageType.STOP_TYPING);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiverId()),
                "/queue/messages",
                message
        );
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage message,
                               SimpMessageHeaderAccessor simpMessageHeaderAccessor) {

        Integer senderId = message.getSenderId();

        Objects.requireNonNull(
                simpMessageHeaderAccessor.getSessionAttributes()).put("senderId", senderId
        );

        if (message.getType() == MessageType.ONLINE) {
            onlineUserService.setOnlineUser(message.getSenderId());
        } else if (message.getType() == MessageType.OFFLINE) {
            onlineUserService.setOfflineUser(message.getSenderId());
        }

        messagingTemplate.convertAndSend("/topic/status", message);
    }
}

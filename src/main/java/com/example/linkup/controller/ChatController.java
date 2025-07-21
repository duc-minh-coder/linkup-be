package com.example.linkup.controller;

import com.example.linkup.dto.request.ChatMessage;
import com.example.linkup.dto.response.ChatMessageResponse;
import com.example.linkup.enums.MessageType;
import com.example.linkup.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    SimpMessagingTemplate messagingTemplate;
    MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        message.setCreatedTime(new Date());

        messageService.saveMessage(message);

        // gửi đến receiver(client sẽ vào /user/{receiverId}/queue/messages
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiverId()),
                "/queue/messages",
                message);
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
//    @SendTo("/topic/public")
    public void addUser(@Payload ChatMessage message,
                               SimpMessageHeaderAccessor simpMessageHeaderAccessor) {

        Objects.requireNonNull(
                simpMessageHeaderAccessor.getSessionAttributes()).put("senderId", message.getSenderId()
        );

        message.setType(MessageType.OFFLINE);
        messagingTemplate.convertAndSend("/topic/status", message);
    }
}

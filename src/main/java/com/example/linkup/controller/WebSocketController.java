package com.example.linkup.controller;

import com.example.linkup.dto.request.MessageRequest;
import com.example.linkup.dto.request.TypingRequest;
import com.example.linkup.dto.response.MessageResponse;
import com.example.linkup.enums.MessageType;
import com.example.linkup.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketController {
//    MessageService messageService;
//    SimpMessagingTemplate messagingTemplate;
//
//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload MessageRequest request) {
//        // Lưu tin nhắn vào database nếu là chat message
//        MessageResponse messageResponse = messageService.sendMessage(request);
//
//        // Gửi tin nhắn đến người nhận
//        messagingTemplate.convertAndSendToUser(
//                String.valueOf(messageResponse.getReceiverId()),
//                "/queue/messages",
//                messageResponse
//        );
//
//        // Gửi lại cho người gửi để xác nhận
////        messagingTemplate.convertAndSendToUser(
////                String.valueOf(messageResponse.getSenderId()),
////                "/queue/messages",
////                messageResponse
////        );
//    }
//
//    @MessageMapping("/chat.typing")
//    public void handleTyping(@Payload TypingRequest request) {
//        MessageResponse typingResponse = MessageResponse.builder()
//                .senderId(request.getSenderId())
//                .receiverId(request.getReceiverId())
//                .type(MessageType.TYPING)
//                .content("is typing...")
//                .build();
//
//        messagingTemplate.convertAndSendToUser(
//                String.valueOf(request.getReceiverId()),
//                "/queue/typing",
//                typingResponse
//        );
//    }
//
//    @MessageMapping("/chat.stopTyping")
//    public void handleStopTyping(@Payload TypingRequest typingRequest) {
//        MessageResponse stopTypingResponse = MessageResponse.builder()
//                .senderId(typingRequest.getSenderId())
//                .receiverId(typingRequest.getReceiverId())
//                .type(MessageType.STOP_TYPING)
//                .content("stopped typing")
//                .build();
//
//        messagingTemplate.convertAndSendToUser(
//                String.valueOf(typingRequest.getReceiverId()),
//                "/queue/typing",
//                stopTypingResponse
//        );
//    }
}

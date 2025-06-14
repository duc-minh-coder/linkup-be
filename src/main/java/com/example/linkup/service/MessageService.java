package com.example.linkup.service;

import com.example.linkup.dto.request.MessageRequest;
import com.example.linkup.dto.response.ConversationResponse;
import com.example.linkup.dto.response.MessageResponse;
import com.example.linkup.entity.Messages;
import com.example.linkup.entity.Users;
import com.example.linkup.enums.MessageType;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.repository.MessageRepository;
import com.example.linkup.repository.ProfileRepository;
import com.example.linkup.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageService {
    MessageRepository messageRepository;
    UserRepository userRepository;

    public MessageResponse sendMessage(MessageRequest request) {
        var content = SecurityContextHolder.getContext();
        String username = content.getAuthentication().getName();

        Users sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Messages message = Messages.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .isRead(false)
                .createdTime(new Date())
                .build();

        Messages savedMessage = messageRepository.save(message);

        log.info("User {} gửi tin nhắn cho {}: {}", sender.getId(), receiver.getId(), request.getContent());

        return MessageResponse.builder()
                .senderId(savedMessage.getSender().getId())
                .senderName(savedMessage.getSender().getProfile().getFullName())
                .receiverId(savedMessage.getReceiver().getId())
                .receiverName(savedMessage.getReceiver().getProfile().getFullName())
                .content(savedMessage.getContent())
                .type(MessageType.CHAT)
                .isRead(false)
                .createdTime(new Date())
                .build();
    }

    public List<ConversationResponse> getListConversation() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Messages> lastMessage = messageRepository.findLastMessageWithEachUser(user.getId());

        return lastMessage.stream().map(message -> {
            // xác định vai trò ng còn lại
            Users otherUser = message.getSender().getId() == user.getId()
                    ? message.getReceiver()
                    : message.getSender();

            return ConversationResponse.builder()
                    .userId(otherUser.getId())
                    .username(otherUser.getProfile().getFullName())
                    .lastMessage(message.getContent())
                    .lastMessageTime(message.getCreatedTime())
                    .isOnline(false)
                    .build();
        }).toList();
    }

    public List<MessageResponse> getConversation(int otherUserId) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Messages> messageList =
                messageRepository.findConversationBetweenUsers(user.getId(), otherUser.getId());

        return messageList.stream().map(message ->
                MessageResponse.builder()
                        .id(message.getId())
                        .senderId(message.getSender().getId())
                        .senderName(message.getSender().getProfile().getFullName())
                        .receiverId(message.getReceiver().getId())
                        .receiverName(message.getReceiver().getProfile().getFullName())
                        .content(message.getContent())
                        .type(MessageType.CHAT)
                        .createdTime(message.getCreatedTime())
                        .isRead(message.isRead())
                    .build()
                ).toList();
    }

    public void deleteConversation(int otherUserId) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        messageRepository.deleteConversation(user.getId(), otherUser.getId());
    }
}

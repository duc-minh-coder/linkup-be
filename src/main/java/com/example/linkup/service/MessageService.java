package com.example.linkup.service;

import com.example.linkup.dto.request.ChatMessage;
import com.example.linkup.dto.request.MessageGetListConversationWithFriendsRequest;
import com.example.linkup.dto.request.MessageRequest;
import com.example.linkup.dto.request.SearchConversationRequest;
import com.example.linkup.dto.response.ConversationResponse;
import com.example.linkup.dto.response.FriendshipResponse;
import com.example.linkup.dto.response.MessageResponse;
import com.example.linkup.entity.Friendships;
import com.example.linkup.entity.Messages;
import com.example.linkup.entity.Users;
import com.example.linkup.entity.keys.KeyFriendships;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class MessageService {
    MessageRepository messageRepository;
    UserRepository userRepository;
    FriendshipService friendshipService;

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
            // xác dinh vai trò ng còn lại
            Users otherUser = message.getSender().getId() == user.getId()
                    ? message.getReceiver()
                    : message.getSender();

            return ConversationResponse.builder()
                    .userId(otherUser.getId())
                    .username(otherUser.getProfile().getFullName())
                    .userAvatarUrl(otherUser.getProfile().getAvatarUrl())
                    .lastMessage(message.getContent())
                    .lastMessageTime(message.getCreatedTime())
                    .isOnline(false)
                    .build();
        }).toList();
    }

    public List<ConversationResponse> getListConversationWithFriends() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<FriendshipResponse> friends = friendshipService.getFriends(user.getId());

        List<ConversationResponse> listConversation = new ArrayList<>();

        for (FriendshipResponse friend : friends) {
            Messages message = messageRepository.findLastMessageBetweenUsers(user.getId(), friend.getId());

            boolean userSentLast = false;
            String lastMessage = null;
            Date lastMessageTime = null;
            boolean isRead = false;

            if (message != null) {
                userSentLast = message.getSender().getId() == user.getId();
                lastMessage = message.getContent();
                lastMessageTime = message.getCreatedTime();
                isRead = message.isRead();
            }
                ConversationResponse conversationResponse = ConversationResponse.builder()
                        .userId(friend.getId())
                        .username(friend.getFullName())
                        .userAvatarUrl(friend.getAvatarUrl())
                        .lastMessage(lastMessage)
                        .lastMessageTime(lastMessageTime)
                        .userSentLast(userSentLast)
                        .isRead(isRead)
                        .build();

                listConversation.add(conversationResponse);
        }

        listConversation.sort((a, b) -> {
            if (a.getLastMessageTime() == null) return 1;
            if (b.getLastMessageTime() == null) return -1;
            return b.getLastMessageTime().compareTo(a.getLastMessageTime());
        });

        return listConversation;
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

    public List<MessageResponse> getConversationWithFriend(int friendId, int page, int size) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users friend = userRepository.findById(friendId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page, size);

        Page<Messages> messageList =
                messageRepository.findConversationBetweenUserWithPaging(user.getId(), friend.getId(), pageable);

        return messageList.stream().map(message -> MessageResponse.builder()
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

    public Page<MessageResponse> getConversationWithPaging(int otherUserId, int page, int size) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page, size);

        Page<Messages> messagePage =
                messageRepository.findConversationBetweenUserWithPaging(user.getId(), otherUser.getId(), pageable);

        return messagePage.map(message ->
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
                .build());
    }

    public long getUnReadCount() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return messageRepository.countUnreadMessages(user.getId());
    }

    public Map<Integer, Long> getUnreadCounts() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<FriendshipResponse> friendshipsList = friendshipService.getFriends(user.getId());

        Map<Integer, Long> unreadCounts = new HashMap<>();

        for (FriendshipResponse friend : friendshipsList) {
            long unread = messageRepository.countUnreadMessagesBetweenUser(friend.getId(), user.getId());

            unreadCounts.put(friend.getId(), unread);
        }

        return unreadCounts;
    }

    public long getUnReadCountBetweenUser(int otherUserId) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return messageRepository.countUnreadMessagesBetweenUser(otherUserId, user.getId());
    }

    public void saveMessage(ChatMessage message) {
        Users sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users receiver = userRepository.findById(message.getReceiverId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Messages messages = Messages.builder()
                .sender(sender)
                .receiver(receiver)
                .content(message.getContent())
                .createdTime(message.getCreatedTime() != null ? message.getCreatedTime() : new Date())
                .isRead(false)
                .build();

        messageRepository.save(messages);
    }

    public void markRead(int otherUserId) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Messages> messagesList = messageRepository.findUnreadMessages(otherUserId, user.getId());

        for (Messages message : messagesList) {
            message.setRead(true);
        }

        messageRepository.saveAll(messagesList);
    }

    public List<ConversationResponse> searchConversation(SearchConversationRequest request, int page, int size) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<FriendshipResponse> listFriend = friendshipService.getFriendWithPagingByText(user.getId(), page, size, request.getText());

        List<ConversationResponse> conversationList = new ArrayList<>();

        for (FriendshipResponse friend : listFriend) {
            Messages message =
                    messageRepository.findLastMessageBetweenUsers(user.getId(), friend.getId());

            if (message == null) continue;

            Users otherUser = message.getSender().getId() == user.getId()
                    ? message.getReceiver()
                    : message.getSender();

            conversationList.add(ConversationResponse.builder()
                    .userId(otherUser.getId())
                    .username(otherUser.getProfile().getFullName())
                    .userAvatarUrl(otherUser.getProfile().getAvatarUrl())
                    .lastMessage(message.getContent())
                    .lastMessageTime(message.getCreatedTime())
                    .userSentLast(message.getSender().getId() == user.getId())
                    .isRead(message.isRead())
                    .build());
        }

        return conversationList;
    }
}

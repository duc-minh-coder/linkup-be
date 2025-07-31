package com.example.linkup.service;

import com.example.linkup.dto.response.CommentResponse;
import com.example.linkup.dto.response.NotificationResponse;
import com.example.linkup.entity.*;
import com.example.linkup.enums.NotificationType;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    UserRepository userRepository;
    PostRepository postRepository;

    public List<NotificationResponse> getListNotification() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Notifications> notificationList = notificationRepository.findByUserId(user.getId());

        return notificationList.stream().map(notification ->
                NotificationResponse.builder()
                        .id(notification.getId())
                        .receiverId(notification.getUser().getId())
                        .actorId(notification.getActor().getId())
                        .actorName(notification.getActor().getProfile().getFullName())
                        .actorAvt(notification.getActor().getProfile().getAvatarUrl())
                        .type(notification.getType())
                        .postId(notification.getPost() != null ? notification.getPost().getId() : null)
                        .commentId(notification.getComment() != null ? notification.getComment().getId() : null)
                        .build()).toList();
    }

    public void createNotification(int receiverId, int actorId, NotificationType type, Posts post, Comments comment) {
        if (receiverId == actorId) return;

        Users receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Users actor = userRepository.findById(actorId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Notifications notification = Notifications.builder()
                .user(receiver)
                .actor(actor)
                .type(type)
                .post(post)
                .comment(comment)
                .createdTime(new Date())
                .build();

        notificationRepository.save(notification);
    }

    public void sendFriendRequestNotification(int receiverId, int actorId) {
        createNotification(receiverId, actorId, NotificationType.FRIEND_REQUEST, null, null);
    }

    public void acceptedRequestNotification(int receiverId, int actorId) {
        createNotification(receiverId, actorId, NotificationType.FRIEND_ACCEPTED, null, null);
    }

    public void likePostNotification(int receiverId, int actorId, int postId) {
        Posts thisPost = postRepository.findById(postId)
                        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        createNotification(receiverId, actorId, NotificationType.POST_LIKE, thisPost, null);
    }

    public void commentPostNotification(int receiverId, int actorId, int postId, Comments comment) {
        Posts thisPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        createNotification(receiverId, actorId, NotificationType.POST_COMMENT, thisPost, comment);
    }
}

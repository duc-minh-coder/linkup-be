package com.example.linkup.service;

import com.example.linkup.dto.request.FriendShipRequest;
import com.example.linkup.dto.request.FriendshipHandlingRequest;
import com.example.linkup.entity.Friendships;
import com.example.linkup.entity.Users;
import com.example.linkup.entity.keys.KeyFriendships;
import com.example.linkup.enums.FriendshipStatus;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.repository.FriendshipRepository;
import com.example.linkup.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendshipService {
    FriendshipRepository friendshipRepository;
    UserRepository userRepository;

    public String sendFriendRequest(FriendShipRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        int senderId = sender.getId();
        int receiverId = receiver.getId();

        if (senderId == receiverId)
            throw new AppException(ErrorCode.INVALID_FRIEND_REQUEST_SENT);

        //ktra mqh đã tồn tại ch
        KeyFriendships key = new KeyFriendships(senderId, receiverId); // đưa thành 1 obj khoá
        var existing = friendshipRepository.findById(key);

        if (existing.isPresent()) {
            FriendshipStatus status = existing.get().getStatus();

            return switch (status) {
                case PENDING -> "đã gửi lời mời kết bạn";
                case ACCEPTED -> "đã kết bạn";
                case REJECTED -> "lời mời bị từ chối";
            };
        }

        Friendships friendships = Friendships.builder()
                .id(key)
                .user(sender)
                .friend(receiver)
                .status(FriendshipStatus.PENDING)
                .createdTime(new Date())
                .build();

        friendshipRepository.save(friendships);

        return "đã gửi lời mời kết bạn";
    }

    public String handlingRequest(FriendshipHandlingRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users receiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        int senderId = sender.getId();
        int receiverId = receiver.getId();

        if (senderId == receiverId)
            throw new AppException(ErrorCode.INVALID_FRIEND_REQUEST_SENT);

        KeyFriendships keyFriendships = new KeyFriendships(senderId, receiverId);
        Friendships friendships = friendshipRepository.findById(keyFriendships)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_FRIEND_REQUEST_SENT));

        if (friendships.getStatus() != FriendshipStatus.PENDING)
            throw new AppException(ErrorCode.FRIEND_REQUEST_NOT_PENDING);

        friendships.setStatus(request.isAccept()
                ? FriendshipStatus.ACCEPTED
                : FriendshipStatus.REJECTED
        );

        friendshipRepository.save(friendships);

        return request.isAccept()
                ? "đã là bạn bè"
                : "đã từ chối";
    }
}

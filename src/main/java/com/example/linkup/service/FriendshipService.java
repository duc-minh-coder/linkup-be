package com.example.linkup.service;

import com.example.linkup.dto.request.FriendShipRequest;
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
}

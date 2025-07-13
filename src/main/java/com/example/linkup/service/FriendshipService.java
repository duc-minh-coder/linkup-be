package com.example.linkup.service;

import com.example.linkup.dto.request.FriendShipRequest;
import com.example.linkup.dto.request.FriendshipHandlingRequest;
import com.example.linkup.dto.response.FriendshipResponse;
import com.example.linkup.entity.Friendships;
import com.example.linkup.entity.Profiles;
import com.example.linkup.entity.Users;
import com.example.linkup.entity.keys.KeyFriendships;
import com.example.linkup.enums.FriendshipStatus;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.repository.FriendshipRepository;
import com.example.linkup.repository.ProfileRepository;
import com.example.linkup.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendshipService {
    FriendshipRepository friendshipRepository;
    UserRepository userRepository;
    ProfileRepository profileRepository;

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

    public String deleteFriendship(int friendId) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users friend = userRepository.findById(friendId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getId() == friend.getId())
            throw new AppException(ErrorCode.INVALID_FRIEND_REQUEST_SENT);

        //ktra qh 2 chiều
        KeyFriendships key1 = new KeyFriendships(user.getId(), friend.getId());
        KeyFriendships key2 = new KeyFriendships(friend.getId(), user.getId());

        var friendship1 = friendshipRepository.findById(key1);
        var friendship2 = friendshipRepository.findById(key2);

        boolean deleted = false;

        if (friendship1.isPresent() && friendship1.get().getStatus() == FriendshipStatus.ACCEPTED) {
            friendshipRepository.delete(friendship1.get());
            deleted = true;
        }

        if (friendship2.isPresent() && friendship2.get().getStatus() == FriendshipStatus.ACCEPTED) {
            friendshipRepository.delete(friendship2.get());
            deleted = true;
        }

        if (!deleted) throw new AppException(ErrorCode.FRIENDSHIP_NOT_FOUND);

        return "đã xoá bạn bè";
    }

    public List<FriendshipResponse> getFriends(int userId) {
        List<Friendships> sentFriendshipList =
                friendshipRepository.findByIdUserIdAndStatus(userId, FriendshipStatus.ACCEPTED);

        List<Friendships> receiverFriendshipList =
                friendshipRepository.findByIdFriendIdAndStatus(userId, FriendshipStatus.ACCEPTED);

        List<Users> friends = new ArrayList<>();

        for (Friendships friendship : sentFriendshipList)
            friends.add(friendship.getFriend());

        for (Friendships friendship : receiverFriendshipList)
            friends.add(friendship.getUser());

        return friends.stream().map(friend -> {
            Profiles profile = profileRepository.findById(friend.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            return FriendshipResponse.builder()
                    .id(friend.getId())
                    .fullName(profile.getFullName())
                    .avatarUrl(profile.getAvatarUrl())
                    .location(profile.getLocation())
                    .build();
        }).toList();
    }

    public boolean isFriend(int friendId) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isFriend = false;

        KeyFriendships key1 = new KeyFriendships(user.getId(), friendId);
        KeyFriendships key2 = new KeyFriendships(friendId, user.getId());

        var friendship1 = friendshipRepository.findById(key1);
        var friendship2 = friendshipRepository.findById(key2);

        if (friendship1.isPresent() || friendship2.isPresent())
            isFriend = true;

        return isFriend;
    }
}

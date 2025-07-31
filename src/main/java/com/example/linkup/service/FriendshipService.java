package com.example.linkup.service;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendshipService {
    FriendshipRepository friendshipRepository;
    UserRepository userRepository;
    ProfileRepository profileRepository;
    NotificationService notificationService;

    public FriendshipStatus sendFriendRequest(FriendshipHandlingRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users receiver = userRepository.findById(request.getOtherUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        KeyFriendships key1 = new KeyFriendships(sender.getId(), receiver.getId());
        KeyFriendships key2 = new KeyFriendships(receiver.getId(), sender.getId());

        var friendship1 = friendshipRepository.findById(key1);
        var friendship2 = friendshipRepository.findById(key2);

        if (friendship1.isPresent() || friendship2.isPresent())
            throw new AppException(ErrorCode.FRIENDSHIP_EXISTED);

        if (request.getStatus() != FriendshipStatus.REQUEST_SENT)
            throw new AppException(ErrorCode.INVALID_FRIEND_REQUEST_STATUS);

        Friendships f1 = Friendships.builder()
                .id(key1)
                .user(sender)
                .friend(receiver)
                .status(FriendshipStatus.REQUEST_SENT)
                .createdTime(new Date())
                .build();

        Friendships f2 = Friendships.builder()
                .id(key2)
                .user(receiver)
                .friend(sender)
                .status(FriendshipStatus.REQUEST_RECEIVED)
                .createdTime(new Date())
                .build();

        friendshipRepository.save(f1);
        friendshipRepository.save(f2);

        notificationService.sendFriendRequestNotification(receiver.getId(), sender.getId());

        return FriendshipStatus.REQUEST_SENT;
    }

    public FriendshipStatus handlingRequest(FriendshipHandlingRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users otherUser = userRepository.findById(request.getOtherUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        KeyFriendships key1 = new KeyFriendships(user.getId(), otherUser.getId());
        KeyFriendships key2 = new KeyFriendships(otherUser.getId(), user.getId());

        var friendship1 = friendshipRepository.findById(key1)
                .orElseThrow(() -> new AppException(ErrorCode.FRIENDSHIP_NOT_FOUND));
        var friendship2 = friendshipRepository.findById(key2)
                .orElseThrow(() -> new AppException(ErrorCode.FRIENDSHIP_NOT_FOUND));

        if (request.getStatus() == FriendshipStatus.FRIEND) {
            friendship1.setStatus(FriendshipStatus.FRIEND);
            friendship2.setStatus(FriendshipStatus.FRIEND);
            friendshipRepository.save(friendship1);
            friendshipRepository.save(friendship2);

            notificationService.acceptedRequestNotification(otherUser.getId(), user.getId());

            return FriendshipStatus.FRIEND;
        }
        else if (request.getStatus() == FriendshipStatus.NOT_FRIEND) {
            friendshipRepository.delete(friendship1);
            friendshipRepository.delete(friendship2);

            return FriendshipStatus.NOT_FRIEND;
        }

        throw new AppException(ErrorCode.INVALID_FRIEND_REQUEST_STATUS);
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

        if (friendship1.isPresent() && friendship2.isPresent()) {
            if (friendship1.get().getStatus() == FriendshipStatus.FRIEND && friendship2.get().getStatus() == FriendshipStatus.FRIEND) {
                friendshipRepository.delete(friendship1.get());
                friendshipRepository.delete(friendship2.get());
                deleted = true;
            }
        }
        if (!deleted)
            throw new AppException(ErrorCode.FRIENDSHIP_NOT_FOUND);

        return "đã xoá bạn bè";
    }

    public List<FriendshipResponse> getFriends(int userId) {
        List<Friendships> friendshipsList =
                friendshipRepository.findByIdUserIdAndStatus(userId, FriendshipStatus.FRIEND);

        List<Users> friends = new ArrayList<>();

        for (Friendships friendship : friendshipsList)
            friends.add(friendship.getFriend());

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

    public List<FriendshipResponse> getFriendWithPaging(int userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Friendships> friendshipsPage =
                friendshipRepository.findFriendByUserIdAndStatusWithPage(userId, FriendshipStatus.FRIEND, pageable);

        List<Users> friends = new ArrayList<>();

        for (Friendships friendship : friendshipsPage) {
            friends.add(friendship.getFriend());
        }

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

    public List<FriendshipResponse> getRequest() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Friendships> requestList =
                friendshipRepository.findByIdUserIdAndStatus(user.getId(), FriendshipStatus.REQUEST_RECEIVED);

        List<Users> userList = new ArrayList<>();

        for (Friendships friendship : requestList)
            userList.add(friendship.getFriend());

        return userList.stream().map(u -> {
            Profiles profile = profileRepository.findById(u.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            return FriendshipResponse.builder()
                    .id(profile.getUserId())
                    .fullName(profile.getFullName())
                    .avatarUrl(profile.getAvatarUrl())
                    .location(profile.getLocation())
                    .build();
        }).toList();
    }

    public List<FriendshipResponse> getRequestWithPaging(int userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Friendships> friendshipsPage =
                friendshipRepository.findFriendByUserIdAndStatusWithPage(userId, FriendshipStatus.REQUEST_RECEIVED, pageable);

        List<Users> requests = new ArrayList<>();

        for (Friendships request : friendshipsPage) {
            requests.add(request.getFriend());
        }

        return requests.stream().map(request -> {
            Profiles profile = profileRepository.findById(request.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            return FriendshipResponse.builder()
                    .id(request.getId())
                    .fullName(profile.getFullName())
                    .avatarUrl(profile.getAvatarUrl())
                    .location(profile.getLocation())
                    .build();
        }).toList();
    }

    public FriendshipStatus checkFriendship(int userId, int otherUserId) {
        if (userId == otherUserId)
            return FriendshipStatus.OWNER;

        KeyFriendships key = new KeyFriendships(userId, otherUserId);

        var friendship = friendshipRepository.findById(key);

        if (friendship.isPresent())
            return friendship.get().getStatus();

        return FriendshipStatus.NOT_FRIEND;
    }
}

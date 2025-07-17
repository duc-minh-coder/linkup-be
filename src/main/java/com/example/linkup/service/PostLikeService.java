package com.example.linkup.service;

import com.example.linkup.dto.response.LikeCountOfPostResponse;
import com.example.linkup.dto.response.PostLikeResponse;
import com.example.linkup.dto.response.UserLikeResponse;
import com.example.linkup.entity.PostLikes;
import com.example.linkup.entity.Posts;
import com.example.linkup.entity.Profiles;
import com.example.linkup.entity.Users;
import com.example.linkup.entity.keys.KeyPostLikes;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.repository.PostLikeRepository;
import com.example.linkup.repository.PostRepository;
import com.example.linkup.repository.ProfileRepository;
import com.example.linkup.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostLikeService {
    PostLikeRepository postLikeRepository;
    UserRepository userRepository;
    PostRepository postRepository;
    ProfileRepository profileRepository;
    SimpMessagingTemplate simpMessagingTemplate;

    public Boolean toggleLike(int postId) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        KeyPostLikes key = new KeyPostLikes(user.getId(), post.getId());
        var existing = postLikeRepository.findById(key);
        boolean isLiked;

        if (existing.isPresent()) {
            postLikeRepository.deleteById(key);

            isLiked = false;
        } else {
            PostLikes postLikes = PostLikes.builder()
                    .id(key)
                    .user(user)
                    .post(post)
                    .createdTime(new Date())
                    .build();

            postLikeRepository.save(postLikes);

            isLiked = true;
        }

//        int likesCount = postLikeRepository.countByPostId(postId);

        // Gửi message về cho client đang subscribe
//        simpMessagingTemplate.convertAndSend("/topic/post-like/" + postId,
//                new LikeCountOfPostResponse(postId, likesCount));

        return isLiked;
    }

    public List<UserLikeResponse> getLikesByPost(int postId) {
        List<PostLikes> postLikesList = postLikeRepository.findAllByPostId(postId);

        return postLikesList.stream().map(postLike -> {
                    Profiles userProfile = profileRepository.findById(postLike.getUser().getId())
                            .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));

                    return UserLikeResponse.builder()
                            .id(userProfile.getUserId())
                            .fullName(userProfile.getFullName())
                            .build();
                }
        ).toList();
    }
}

package com.example.linkup.service;

import com.example.linkup.dto.response.PostLikeResponse;
import com.example.linkup.entity.PostLikes;
import com.example.linkup.entity.Posts;
import com.example.linkup.entity.Users;
import com.example.linkup.entity.keys.KeyPostLikes;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.repository.PostLikeRepository;
import com.example.linkup.repository.PostRepository;
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
public class PostLikeService {
    PostLikeRepository postLikeRepository;
    UserRepository userRepository;
    PostRepository postRepository;

    public PostLikeResponse toggleLike(int postId) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        KeyPostLikes key = new KeyPostLikes(user.getId(), post.getId());
        var existing = postLikeRepository.findById(key);

        PostLikes postLikes = null;
        if (existing.isPresent()) {
            postLikeRepository.deleteById(key);

            return PostLikeResponse.builder()
                    .userId(user.getId())
                    .postId(post.getId())
                    .createdTime(new Date())
                    .build();
        } else {
            postLikes = PostLikes.builder()
                    .id(key)
                    .user(user)
                    .post(post)
                    .createdTime(new Date())
                    .build();

            postLikeRepository.save(postLikes);

            return PostLikeResponse.builder()
                    .userId(postLikes.getUser().getId())
                    .postId(postLikes.getPost().getId())
                    .createdTime(postLikes.getCreatedTime())
                    .build();
        }
    }
}

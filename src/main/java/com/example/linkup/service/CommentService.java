package com.example.linkup.service;

import com.example.linkup.dto.request.CommentRequest;
import com.example.linkup.dto.response.CommentResponse;
import com.example.linkup.entity.Comments;
import com.example.linkup.entity.Posts;
import com.example.linkup.entity.Profiles;
import com.example.linkup.entity.Users;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.repository.CommentRepository;
import com.example.linkup.repository.PostRepository;
import com.example.linkup.repository.ProfileRepository;
import com.example.linkup.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    ProfileRepository profileRepository;
    PostRepository postRepository;
    UserRepository userRepository;
    NotificationService notificationService;

    public CommentResponse createComment(CommentRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Posts post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        Profiles profile = profileRepository.findById(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));

        Comments parentComment = null;

        Comments comments = Comments.builder()
                .author(user)
                .post(post)
                .content(request.getContent())
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();

        Comments commentsSaved = commentRepository.save(comments);

        notificationService.commentPostNotification(
                post.getAuthor().getId(),
                user.getId(),
                post.getId(),
                commentsSaved
        );

        return CommentResponse.builder()
                .id(commentsSaved.getId())
                .fullName(profile.getFullName())
                .avatarUrl(profile.getAvatarUrl())
                .authorId(commentsSaved.getAuthor().getId())
                .postId(commentsSaved.getPost().getId())
                .content(commentsSaved.getContent())
                .updatedTime(commentsSaved.getUpdatedTime())
                .build();
    }

    public List<CommentResponse> getCommentsOfPost(int postId) {
         List<Comments> comments = commentRepository.findAllByPostId(postId);

         return comments.stream().map(comment -> {
             Profiles profile = comment.getAuthor().getProfile();

             return CommentResponse.builder()
                     .id(comment.getId())
                     .authorId(comment.getAuthor().getId())
                     .avatarUrl(profile.getAvatarUrl())
                     .fullName(profile.getFullName())
                     .postId(comment.getPost().getId())
                     .content(comment.getContent())
                     .updatedTime(comment.getUpdatedTime())
                     .build();
         }).toList();
    }
}

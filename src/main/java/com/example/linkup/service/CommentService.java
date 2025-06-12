package com.example.linkup.service;

import com.example.linkup.dto.request.CommentRequest;
import com.example.linkup.dto.response.CommentResponse;
import com.example.linkup.entity.Comments;
import com.example.linkup.entity.Posts;
import com.example.linkup.entity.Users;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.repository.CommentRepository;
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
public class CommentService {
    CommentRepository commentRepository;
    UserRepository userRepository;
    PostRepository postRepository;

    public CommentResponse createComment(CommentRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Posts post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        Comments parentComment = null;

        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
        }

        Comments comments = Comments.builder()
                .author(user)
                .post(post)
                .content(request.getContent())
                .createdTime(new Date())
                .updatedTime(new Date())
                .parentComment(parentComment)
                .build();

        Comments commentsSaved = commentRepository.save(comments);

        return CommentResponse.builder()
                .id(commentsSaved.getId())
                .parentCommentId(commentsSaved.getParentComment() != null
                        ? commentsSaved.getParentComment().getId()
                        : null
                )
                .authorId(commentsSaved.getAuthor().getId())
                .postId(commentsSaved.getPost().getId())
                .content(comments.getContent())
                .updatedTime(commentsSaved.getUpdatedTime())
                .build();
    }
}

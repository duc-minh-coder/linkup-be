package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.request.CommentRequest;
import com.example.linkup.dto.response.CommentResponse;
import com.example.linkup.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping
    public ApiResponse<CommentResponse> createComment(@RequestBody CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.createComment(request))
                .build();
    }

    @GetMapping("/{postId}")
    public ApiResponse<List<CommentResponse>> getCommentsOfPost(@PathVariable int postId) {
        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentService.getCommentsOfPost(postId))
                .build();
    }
}

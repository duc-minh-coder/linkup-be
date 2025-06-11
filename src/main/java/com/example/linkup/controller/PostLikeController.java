package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.response.PostLikeResponse;
import com.example.linkup.service.PostLikeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post-like")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostLikeController {
    PostLikeService postLikeService;

    @PostMapping
    public ApiResponse<PostLikeResponse> toggleLike(@RequestParam int postId) {
        return ApiResponse.<PostLikeResponse>builder()
                .result(postLikeService.toggleLike(postId))
                .build();
    }
}

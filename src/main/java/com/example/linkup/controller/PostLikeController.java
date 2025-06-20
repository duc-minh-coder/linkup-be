package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.response.PostLikeResponse;
import com.example.linkup.dto.response.UserLikeResponse;
import com.example.linkup.entity.Users;
import com.example.linkup.service.PostLikeService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post-like")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostLikeController {
    PostLikeService postLikeService;

    @PostMapping
    public ApiResponse<Boolean> toggleLike(@RequestParam int postId) {
        return ApiResponse.<Boolean>builder()
                .result(postLikeService.toggleLike(postId))
                .build();
    }

    @PostMapping("/post")
    public ApiResponse<List<UserLikeResponse>> getLikesByPost(@RequestParam int postId) {
        return ApiResponse.<List<UserLikeResponse>>builder()
                .result(postLikeService.getLikesByPost(postId))
                .build();
    }
}

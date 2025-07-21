package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.request.PostRequest;
import com.example.linkup.dto.request.UpdatePostRequest;
import com.example.linkup.dto.response.PostResponse;
import com.example.linkup.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PostController {
    PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> createPost(@ModelAttribute PostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.createPost(request))
                .build();
    }

    @GetMapping("/user")
    public ApiResponse<List<PostResponse>> getAllUrPost() {
        return ApiResponse.<List<PostResponse>>builder()
                .result(postService.getAllUrPost())
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<PostResponse>> getPostsByUserId(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<List<PostResponse>>builder()
                .result(postService.getPostsByUserId(userId, page, size))
                .build();
    }

    @GetMapping("/all-post")
    public ApiResponse<List<PostResponse>> getAllPostOfFriends(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<List<PostResponse>>builder()
                .result(postService.getPostOfFriends(page, size))
                .build();
    }

    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> updatePost(@PathVariable int postId, @ModelAttribute UpdatePostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.updatePost(postId, request))
                .build();
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<String> deletePost(@PathVariable int postId) {
        postService.deletePost(postId);

        return ApiResponse.<String>builder()
                .result("post has been deleted!")
                .build();
    }

    @GetMapping("/friend-posts")
    public ApiResponse<List<PostResponse>> getPosts() {
        return ApiResponse.<List<PostResponse>>builder()
                .result(postService.getPosts())
                .build();
    }

    @PostMapping("/share")
    public ApiResponse<PostResponse> sharePost(@RequestParam int postId) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.sharePost(postId))
                .build();
    }
}

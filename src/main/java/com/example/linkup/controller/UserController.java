package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.request.UserCreationRequest;
import com.example.linkup.dto.request.UserUpdatePasswordRequest;
import com.example.linkup.dto.response.UserResponse;
import com.example.linkup.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable int userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping("")
    ApiResponse<List<UserResponse>> getAllUser() {
        List<UserResponse> userResponseList = userService.getAllUser();

        return ApiResponse.<List<UserResponse>>builder()
                .result(userResponseList)
                .build();
    }

    @PatchMapping("")
    ApiResponse<UserResponse> updatePassword(@RequestBody UserUpdatePasswordRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updatePassword(request))
                .build();
    }
}

package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.service.OnlineUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/online")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OnlineUserController {
    OnlineUserService onlineUserService;

    @GetMapping()
    ApiResponse<List<Integer>> getOnlineUserIds() {
        return ApiResponse.<List<Integer>>builder()
                .result(onlineUserService.getOnlineUserIds())
                .build();
    }

    @GetMapping("/checking/{userId}")
    ApiResponse<Boolean> checkOnlineUser(@PathVariable int userId) {
        return ApiResponse.<Boolean>builder()
                .result(onlineUserService.isUserOnline(userId))
                .build();
    }
}

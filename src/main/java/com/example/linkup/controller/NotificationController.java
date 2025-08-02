package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.response.NotificationResponse;
import com.example.linkup.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @GetMapping()
    ApiResponse<List<NotificationResponse>> getListNotification() {
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(notificationService.getListNotification())
                .build();
    }

    @PostMapping("/{notificationId}/read")
    ApiResponse<String> markAsRead(@PathVariable int notificationId) {
        return ApiResponse.<String>builder()
                .result(notificationService.markAsRead(notificationId))
                .build();
    }

    @PostMapping("/read-all")
    ApiResponse<String> markAllRead() {
        return ApiResponse.<String>builder()
                .result(notificationService.markAllRead())
                .build();
    }
}

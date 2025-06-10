package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.request.FriendShipRequest;
import com.example.linkup.dto.request.FriendshipHandlingRequest;
import com.example.linkup.service.FriendshipService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendship")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendshipController {
    FriendshipService friendshipService;

    @PostMapping
    public ApiResponse<String> sendFriendRequest(@RequestBody FriendShipRequest request) {
        String message = friendshipService.sendFriendRequest(request);

        return ApiResponse.<String>builder()
                .result(message)
                .build();
    }

    @PostMapping("/handling")
    public ApiResponse<String> handlingRequest(@RequestBody FriendshipHandlingRequest request) {
        String message = friendshipService.handlingRequest(request);

        return ApiResponse.<String>builder()
                .result(message)
                .build();
    }
}

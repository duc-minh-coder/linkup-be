package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.request.FriendShipRequest;
import com.example.linkup.dto.request.FriendshipHandlingRequest;
import com.example.linkup.dto.response.FriendshipResponse;
import com.example.linkup.enums.FriendshipStatus;
import com.example.linkup.service.FriendshipService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendshipController {
    FriendshipService friendshipService;

    @PostMapping("/send")
    public ApiResponse<FriendshipStatus> sendFriendRequest(@RequestBody FriendshipHandlingRequest request) {
        return ApiResponse.<FriendshipStatus>builder()
                .result(friendshipService.sendFriendRequest(request))
                .build();
    }

    @PostMapping("/handling")
    public ApiResponse<FriendshipStatus> handlingRequest(@RequestBody FriendshipHandlingRequest request) {
        return ApiResponse.<FriendshipStatus>builder()
                .result(friendshipService.handlingRequest(request))
                .build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<String> deleteFriendship(@RequestParam int friendId) {
        String message = friendshipService.deleteFriendship(friendId);

        return ApiResponse.<String>builder()
                .result(message)
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<FriendshipResponse>> getFriends(@PathVariable int userId) {
        return ApiResponse.<List<FriendshipResponse>>builder()
                .result(friendshipService.getFriends(userId))
                .build();
    }

    @GetMapping("/friend/{userId}")
    public ApiResponse<List<FriendshipResponse>> getFriendWithPaging(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<List<FriendshipResponse>>builder()
                .result(friendshipService.getFriendWithPaging(userId, page, size))
                .build();
    }

    @GetMapping("/user/request")
    public ApiResponse<List<FriendshipResponse>> getRequest() {
        return ApiResponse.<List<FriendshipResponse>>builder()
                .result(friendshipService.getRequest())
                .build();
    }
}

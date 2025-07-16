package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.request.MessageGetListConversationWithFriendsRequest;
import com.example.linkup.dto.request.MessageRequest;
import com.example.linkup.dto.response.ConversationResponse;
import com.example.linkup.dto.response.MessageResponse;
import com.example.linkup.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageController {
    MessageService messageService;
    SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public ApiResponse<MessageResponse> sendMessage(@RequestBody MessageRequest request) {
        MessageResponse message = messageService.sendMessage(request);

        // gửi qua ws
        messagingTemplate.convertAndSendToUser(
                String.valueOf(request.getReceiverId()),
                "/queue/messages",
                message
        );

        return ApiResponse.<MessageResponse>builder()
                .result(message)
                .build();
    }

    @GetMapping("/conversation/user")
    // lấy danh sách các cuộc hội thoại
    public ApiResponse<List<ConversationResponse>> getListConversation() {
        return ApiResponse.<List<ConversationResponse>>builder()
                .result(messageService.getListConversation())
                .build();
    }

    @GetMapping("/conversation")
    ApiResponse<List<ConversationResponse>> getListConversationWithFriends() {
        return ApiResponse.<List<ConversationResponse>>builder()
                .result(messageService.getListConversationWithFriends())
                .build();
    }

//    @GetMapping("")
//    // chi tiết 1 cuộc hội thoại vs 1 otherUser
//    public ApiResponse<List<MessageResponse>> getConversation(@PathVariable int otherUserId) {
//        return ApiResponse.<List<MessageResponse>>builder()
//                .result(messageService.getConversation(otherUserId))
//                .build();
//    }

    @GetMapping("/conversation/{friendId}")
    public ApiResponse<List<MessageResponse>> getConversationWithFriend(
            @PathVariable int friendId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<List<MessageResponse>>builder()
                .result(messageService.getConversationWithFriend(friendId, page, size))
                .build();
    }

    @DeleteMapping("/conversation/delete")
    public ApiResponse<Void> deleteConversation(@RequestParam int otherUserId) {
        messageService.deleteConversation(otherUserId);

        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/conversation/paging/{otherUserId}")
    public ApiResponse<Page<MessageResponse>> getConversationWithPaging(@PathVariable int otherUserId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<Page<MessageResponse>>builder()
                .result(messageService.getConversationWithPaging(otherUserId, page, size))
                .build();
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnReadCount() {
        return ApiResponse.<Long>builder()
                .result(messageService.getUnReadCount())
                .build();
    }
}

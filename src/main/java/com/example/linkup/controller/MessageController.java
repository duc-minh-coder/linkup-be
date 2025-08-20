package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.request.MessageGetListConversationWithFriendsRequest;
import com.example.linkup.dto.request.MessageRequest;
import com.example.linkup.dto.request.SearchConversationRequest;
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
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageController {
    MessageService messageService;
    SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public ApiResponse<MessageResponse> sendMessage(@RequestBody MessageRequest request) {
        MessageResponse message = messageService.sendMessage(request);

        // gửi tn đến ng nhận
        messagingTemplate.convertAndSendToUser(
                String.valueOf(request.getReceiverId()),
                "/queue/messages",
                message
        );

        // gửi lại cho user để cập nhật UI
        MessageResponse senderMessage = message.toBuilder()

                .build();

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

    @PostMapping("/conversation/search")
    public ApiResponse<List<ConversationResponse>> searchConversation(
            @RequestBody SearchConversationRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ) {
        return ApiResponse.<List<ConversationResponse>>builder()
                .result(messageService.searchConversation(request, page, size))
                .build();
    }

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

    @GetMapping("/unread-counts")
    public ApiResponse<Map<Integer, Long>> getUnReadCount() {
        return ApiResponse.<Map<Integer, Long>>builder()
                .result(messageService.getUnreadCounts())
                .build();
    }

    @PostMapping("/mark-read")
    ApiResponse<Void> markRead(@RequestParam int otherUserId) {
        messageService.markRead(otherUserId);
        return ApiResponse.<Void>builder().build();
    }
}

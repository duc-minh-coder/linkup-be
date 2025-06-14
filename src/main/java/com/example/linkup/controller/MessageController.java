package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.request.MessageRequest;
import com.example.linkup.dto.response.ConversationResponse;
import com.example.linkup.dto.response.MessageResponse;
import com.example.linkup.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageController {
    MessageService messageService;

    @PostMapping
    public ApiResponse<MessageResponse> sendMessage(@RequestBody MessageRequest request) {
        return ApiResponse.<MessageResponse>builder()
                .result(messageService.sendMessage(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ConversationResponse>> getListConversation() {
        return ApiResponse.<List<ConversationResponse>>builder()
                .result(messageService.getListConversation())
                .build();
    }

    @GetMapping("/{otherUserId}")
    public ApiResponse<List<MessageResponse>> getConversation(@PathVariable int otherUserId) {
        return ApiResponse.<List<MessageResponse>>builder()
                .result(messageService.getConversation(otherUserId))
                .build();
    }

    @DeleteMapping
    public ApiResponse<Void> deleteConversation(@RequestParam int otherUserId) {
        messageService.deleteConversation(otherUserId);

        return ApiResponse.<Void>builder().build();
    }


}

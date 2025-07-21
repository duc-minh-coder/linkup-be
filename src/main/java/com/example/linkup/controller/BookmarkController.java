package com.example.linkup.controller;

import com.example.linkup.dto.request.ApiResponse;
import com.example.linkup.dto.request.BookmarkRequest;
import com.example.linkup.dto.response.BookmarkResponse;
import com.example.linkup.service.BookmarkService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookmarkController {
    BookmarkService bookmarkService;

    @PostMapping("/create")
    public ApiResponse<String> updateBookmark(@RequestBody BookmarkRequest request) {
        return ApiResponse.<String>builder()
                .result(bookmarkService.updateBookmark(request))
                .build();
    }

    @GetMapping("/list")
    public ApiResponse<List<BookmarkResponse>> getBookmarkList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<List<BookmarkResponse>>builder()
                .result(bookmarkService.getBookmarkList(page, size))
                .build();
    }
}

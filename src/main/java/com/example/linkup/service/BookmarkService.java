package com.example.linkup.service;

import com.example.linkup.dto.request.BookmarkRequest;
import com.example.linkup.dto.response.BookmarkResponse;
import com.example.linkup.dto.response.PostResponse;
import com.example.linkup.entity.Bookmarks;
import com.example.linkup.entity.Posts;
import com.example.linkup.entity.Users;
import com.example.linkup.entity.keys.KeyBookmarks;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.repository.BookmarkRepository;
import com.example.linkup.repository.PostRepository;
import com.example.linkup.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookmarkService {
    BookmarkRepository bookmarkRepository;
    UserRepository userRepository;
    PostService postService;
    PostRepository postRepository;

    public String updateBookmark(BookmarkRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Posts post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        KeyBookmarks key = new KeyBookmarks(user.getId(), post.getId());

        var existing = bookmarkRepository.findById(key);

        if (existing.isPresent()) {
            bookmarkRepository.deleteById(key);

            return "đã huỷ lưu";
        }

        Bookmarks bookmark = Bookmarks.builder()
                .id(key)
                .user(user)
                .post(post)
                .createdTime(new Date())
                .build();

        bookmarkRepository.save(bookmark);

        return "đã lưu";
    }

    public List<BookmarkResponse> getBookmarkList(int page, int size) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page, size);

        Page<Bookmarks> bookmarksPage = bookmarkRepository.findByUserId(user.getId(), pageable);



        return bookmarksPage.map(bookmark -> {
            PostResponse post = postService.getPostById(bookmark.getPost().getId());

            return BookmarkResponse.builder()
                    .postResponse(post)
                    .createdTime(bookmark.getCreatedTime())
                    .build();
        }).toList();
    }
}

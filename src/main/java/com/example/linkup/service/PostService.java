package com.example.linkup.service;

import com.example.linkup.dto.request.PostMediaRequest;
import com.example.linkup.dto.request.PostRequest;
import com.example.linkup.dto.request.UpdatePostRequest;
import com.example.linkup.dto.response.FriendshipResponse;
import com.example.linkup.dto.response.PostResponse;
import com.example.linkup.entity.Friendships;
import com.example.linkup.entity.PostMedia;
import com.example.linkup.entity.Posts;
import com.example.linkup.entity.Users;
import com.example.linkup.enums.MediaType;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.mapper.PostMapper;
import com.example.linkup.repository.PostRepository;
import com.example.linkup.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PostService {
    PostRepository postRepository;
    UserRepository userRepository;
    CloudinaryService cloudinaryService;
    PostMapper postMapper;
    FriendshipService friendshipService;

    public PostResponse createPost(PostRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Posts post = Posts.builder()
                .content(request.getContent())
                .createdTime(new Date())
                .updatedTime(new Date())
                .author(user)
                .build();

        List<PostMedia> mediaList = new ArrayList<>();
        List<MultipartFile> files = request.getMediaList();

        if (files != null && !files.isEmpty()) {
            int index = 0;

            for (MultipartFile file : files) {
                String fileUrl;

                try {
                    fileUrl = cloudinaryService.uploadFile(file);
                } catch (IOException e) {
                    throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
                }

                // xác định media
                String contentType = file.getContentType();
                MediaType mediaType = null;

                if (contentType != null)
                    mediaType = contentType.startsWith("video")
                            ? MediaType.VIDEO
                            : MediaType.IMAGE;

                PostMedia postMedia = PostMedia.builder()
                        .url(fileUrl)
                        .mediaType(mediaType)
                        .orderIndex((short) index++)
                        .post(post)
                        .build();

                mediaList.add(postMedia);
            }
        }

        post.setPostMedia(mediaList);
        Posts savePost = postRepository.save(post);

        return PostResponse.builder()
                .id(savePost.getId())
                .content(savePost.getContent())
                .createdTime(savePost.getCreatedTime())
                .updatedTime(savePost.getUpdatedTime())
                .postMedia(savePost.getPostMedia().stream()
                        .map(postMapper::postMediaToPostMediaResponse).toList())
                .build();
    }

    public List<PostResponse> getAllUrPost() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        int authorId = user.getId();

        List<Posts> postList = postRepository.findAllByAuthorId(authorId);

        return postList.stream()
                .map(post -> PostResponse.builder()
                    .id(post.getId())
                    .content(post.getContent())
                    .createdTime(post.getCreatedTime())
                    .updatedTime(post.getUpdatedTime())
                    .postMedia(post.getPostMedia().stream()
                            .map(postMapper::postMediaToPostMediaResponse).toList())
                    .build()).toList();
    }

    public PostResponse updatePost(int postId, UpdatePostRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        if (user.getId() != post.getAuthor().getId())
            throw new AppException(ErrorCode.UNAUTHORIZED);

        // sửa content
        if (request.getContent() != null) {
            post.setContent(request.getContent());
            post.setUpdatedTime(new Date());
        }

        List<Integer> listDeleteMediaId = request.getListDeleteMediaId();

        // xoá đi những file muốn xoá
        if (listDeleteMediaId != null && !listDeleteMediaId.isEmpty()) {
            post.getPostMedia().removeIf(postMedia ->
                    listDeleteMediaId.contains(postMedia.getId()));
        }

        //upload file ms
        List<MultipartFile> mediaList = request.getMediaList();
        if (mediaList != null && !mediaList.isEmpty()) {
            int currentIndex = 0;

            for (PostMedia lastIndex : post.getPostMedia()) {
                currentIndex = lastIndex.getOrderIndex();
            }
            ++currentIndex;

            for (MultipartFile file : mediaList) {
                String fileUrl;

                try {
                    fileUrl = cloudinaryService.uploadFile(file);
                }  catch (IOException e) {
                    throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
                }

                String contentType = file.getContentType();
                MediaType mediaType = null;
                if (contentType != null) {
                    mediaType = contentType.startsWith("video")
                            ? MediaType.VIDEO
                            : MediaType.IMAGE;
                }

                PostMedia media = PostMedia.builder()
                        .url(fileUrl)
                        .mediaType(mediaType)
                        .orderIndex((short) currentIndex++)
                        .post(post)
                        .build();

                post.getPostMedia().add(media);
            }
        }

        Posts updatedPost = postRepository.save(post);

        return PostResponse.builder()
                .id(updatedPost.getId())
                .content(updatedPost.getContent())
                .createdTime(updatedPost.getCreatedTime())
                .updatedTime(updatedPost.getUpdatedTime())
                .postMedia(updatedPost.getPostMedia().stream()
                        .map(postMapper::postMediaToPostMediaResponse).toList())
                .build();
    }

    public void deletePost(int id) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Posts post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        if (user.getId() != post.getAuthor().getId())
            throw new AppException(ErrorCode.UNAUTHORIZED);

        postRepository.deleteById(post.getId());
    }

    public List<PostResponse> getPosts() {
        List<FriendshipResponse> friendshipResponseList = friendshipService.getFriends();

        List<Integer> friendIds = new ArrayList<>();

        for (FriendshipResponse friend : friendshipResponseList)
            friendIds.add(friend.getId());

        List<Posts> listFriendPost = new ArrayList<>();

        for (int friendId : friendIds) {
            List<Posts> postsList = postRepository.findAllByAuthorId(friendId);

            listFriendPost.addAll(postsList);
        }

        return listFriendPost.stream().map(post -> PostResponse.builder()
                .id(post.getId())
                .authorId(post.getAuthor().getId())
                .content(post.getContent())
                .createdTime(post.getCreatedTime())
                .updatedTime(post.getUpdatedTime())
                .postMedia(post.getPostMedia().stream()
                        .map(postMapper::postMediaToPostMediaResponse).toList())
                .build()).toList();
    }
}

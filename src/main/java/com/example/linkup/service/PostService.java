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
import com.example.linkup.entity.keys.KeyPostLikes;
import com.example.linkup.enums.FriendshipStatus;
import com.example.linkup.enums.MediaType;
import com.example.linkup.exception.AppException;
import com.example.linkup.exception.ErrorCode;
import com.example.linkup.mapper.PostMapper;
import com.example.linkup.repository.PostLikeRepository;
import com.example.linkup.repository.PostRepository;
import com.example.linkup.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    PostLikeService postLikeService;
    CommentService commentService;
    PostLikeRepository postLikeRepository;

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
                .authorName(user.getProfile().getFullName())
                .authorAvatarUrl(user.getProfile().getAvatarUrl())
                .content(savePost.getContent())
                .userLikes(null)
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

        List<Posts> postList = postRepository.getAllByAuthorId(authorId);

        return postList.stream()
                .map(post -> PostResponse.builder()
                    .id(post.getId())
                    .authorId(authorId)
                    .authorName(user.getProfile().getFullName())
                    .authorAvatarUrl(user.getProfile().getAvatarUrl())
                    .content(post.getContent())
                    .createdTime(post.getCreatedTime())
                    .updatedTime(post.getUpdatedTime())
                    .postMedia(post.getPostMedia().stream()
                            .map(postMapper::postMediaToPostMediaResponse).toList())
                    .userLikes(postLikeService.getLikesByPost(post.getId()))
                    .comments(commentService.getCommentsOfPost(post.getId()))
                    .build()).toList();
    }

    public List<PostResponse> getPostsByUserId(int userId, int page, int size) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users thisUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        FriendshipStatus friendshipStatus = friendshipService.checkFriendship(thisUser.getId(), user.getId());

        if (friendshipStatus == FriendshipStatus.FRIEND || friendshipStatus == FriendshipStatus.OWNER) {
            Pageable pageable = PageRequest.of(page, size);

            Page<Posts> postsPage = postRepository.findPostByUserId(user.getId(), pageable);

            return postsPage.map(post -> {
                KeyPostLikes key = new KeyPostLikes(user.getId(), post.getId());
                var isLike = postLikeRepository.findById(key);

                return PostResponse.builder()
                        .id(post.getId())
                        .authorId(post.getAuthor().getId())
                        .authorName(post.getAuthor().getProfile().getFullName())
                        .authorAvatarUrl(post.getAuthor().getProfile().getAvatarUrl())
                        .content(post.getContent())
                        .postMedia(post.getPostMedia().stream()
                                .map(postMapper::postMediaToPostMediaResponse).toList())
                        .createdTime(post.getCreatedTime())
                        .updatedTime(post.getUpdatedTime())
                        .userLikes(postLikeService.getLikesByPost(post.getId()))
                        .comments(commentService.getCommentsOfPost(post.getId()))
                        .isLiked(isLike.isPresent())
                        .build();
            }).toList();
        }

        return new ArrayList<>();
    }

    public List<PostResponse> getAllPostByUserId(int userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Posts> postList = postRepository.getAllByAuthorId(userId);

        return postList.stream()
                .map(post -> PostResponse.builder()
                        .id(post.getId())
                        .authorId(userId)
                        .authorName(user.getProfile().getFullName())
                        .authorAvatarUrl(user.getProfile().getAvatarUrl())
                        .content(post.getContent())
                        .createdTime(post.getCreatedTime())
                        .updatedTime(post.getUpdatedTime())
                        .postMedia(post.getPostMedia().stream()
                                .map(postMapper::postMediaToPostMediaResponse).toList())
                        .userLikes(postLikeService.getLikesByPost(post.getId()))
                        .comments(commentService.getCommentsOfPost(post.getId()))
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
                .userLikes(postLikeService.getLikesByPost(updatedPost.getId()))
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
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<FriendshipResponse> friendshipResponseList = friendshipService.getFriends(user.getId());

        List<Integer> friendIds = new ArrayList<>();

        for (FriendshipResponse friend : friendshipResponseList)
            friendIds.add(friend.getId());

        List<Posts> listFriendPost = new ArrayList<>();

        for (int friendId : friendIds) {
            List<Posts> postsList = postRepository.getAllByAuthorId(friendId);

            listFriendPost.addAll(postsList);
        }

        listFriendPost.sort((a, b) ->
                b.getCreatedTime().compareTo(a.getCreatedTime()));

        return listFriendPost.stream().map(post -> {
            KeyPostLikes key = new KeyPostLikes(user.getId(), post.getId());
            var isLike = postLikeRepository.findById(key);

            return PostResponse.builder()
                .id(post.getId())
                .authorId(post.getAuthor().getId())
                .authorName(post.getAuthor().getProfile().getFullName())
                .authorAvatarUrl(post.getAuthor().getProfile().getAvatarUrl())
                .isLiked(isLike.isPresent())
                .content(post.getContent())
                .createdTime(post.getCreatedTime())
                .updatedTime(post.getUpdatedTime())
                .postMedia(post.getPostMedia().stream()
                        .map(postMapper::postMediaToPostMediaResponse).toList())
                .userLikes(postLikeService.getLikesByPost(post.getId()))
                .comments(commentService.getCommentsOfPost(post.getId()))
                .build();
        }).toList();
    }

    public List<PostResponse> getPostOfFriends(int page, int size) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<FriendshipResponse> friendList = friendshipService.getFriends(user.getId());

        List<Integer> userIds = new ArrayList<>();

        for (FriendshipResponse friend : friendList)
            userIds.add(friend.getId());

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());

        Page<Posts> postsPage = postRepository.findPostByUserIds(userIds, pageable);

        return postsPage.map(post -> {
            KeyPostLikes key = new KeyPostLikes(user.getId(), post.getId());
            var isLike = postLikeRepository.findById(key);

            return PostResponse.builder()
                    .id(post.getId())
                    .authorId(post.getAuthor().getId())
                    .authorName(post.getAuthor().getProfile().getFullName())
                    .authorAvatarUrl(post.getAuthor().getProfile().getAvatarUrl())
                    .content(post.getContent())
                    .postMedia(post.getPostMedia().stream()
                            .map(postMapper::postMediaToPostMediaResponse).toList())
                    .createdTime(post.getCreatedTime())
                    .updatedTime(post.getUpdatedTime())
                    .userLikes(postLikeService.getLikesByPost(post.getId()))
                    .comments(commentService.getCommentsOfPost(post.getId()))
                    .isLiked(isLike.isPresent())
                    .build();
        }).toList();
    }

    public PostResponse sharePost(int postId) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        Posts sharePost = Posts.builder()
                .author(user)
                .content(post.getContent())
                .originalPost(post)
                .updatedTime(new Date())
                .createdTime(new Date())
                .build();

        List<PostMedia> shareMediaList = new ArrayList<>();

        for (PostMedia media : post.getPostMedia()) {
            PostMedia postMediaItem = PostMedia.builder()
                    .url(media.getUrl())
                    .mediaType(media.getMediaType())
                    .orderIndex(media.getOrderIndex())
                    .post(post)
                    .build();

            shareMediaList.add(postMediaItem);
        }

        sharePost.setPostMedia(shareMediaList);

        Posts savedPost = postRepository.save(sharePost);

        return PostResponse.builder()
                .id(savedPost.getId())
                .content(savedPost.getContent())
                .authorId(savedPost.getAuthor().getId())
                .authorName(savedPost.getAuthor().getProfile().getFullName())
                .authorAvatarUrl(savedPost.getAuthor().getProfile().getAvatarUrl())
                .createdTime(savedPost.getCreatedTime())
                .updatedTime(savedPost.getUpdatedTime())
                .originalPostId(post.getId())
                .postMedia(savedPost.getPostMedia().stream()
                        .map(postMapper::postMediaToPostMediaResponse).toList())
                .userLikes(postLikeService.getLikesByPost(savedPost.getId()))
                .comments(commentService.getCommentsOfPost(savedPost.getId()))
                .build();
    }
}

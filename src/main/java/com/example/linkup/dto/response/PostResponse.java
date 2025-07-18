package com.example.linkup.dto.response;

import com.example.linkup.entity.Users;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    int id;

    int authorId;

    String authorName;

    String authorAvatarUrl;

    String content;

    boolean isLiked;

    boolean saved;

    Integer originalPostId;

    List<PostMediaResponse> postMedia;

    List<UserLikeResponse> userLikes;

    List<CommentResponse> comments;

    Date createdTime;

    Date updatedTime;
}

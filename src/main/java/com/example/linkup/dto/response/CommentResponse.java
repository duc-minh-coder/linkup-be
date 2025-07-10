package com.example.linkup.dto.response;

import com.example.linkup.entity.Comments;
import com.example.linkup.entity.Posts;
import com.example.linkup.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    int id;
    int postId;
    int authorId;
    String avatarUrl;
    String fullName;
    String content;
    Date updatedTime;
}

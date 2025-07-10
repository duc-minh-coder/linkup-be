package com.example.linkup.dto.request;

import com.example.linkup.entity.Comments;
import com.example.linkup.entity.Posts;
import com.example.linkup.entity.Users;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
    int postId;
    String content;
}

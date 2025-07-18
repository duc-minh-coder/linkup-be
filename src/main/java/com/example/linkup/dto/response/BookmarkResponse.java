package com.example.linkup.dto.response;

import com.example.linkup.entity.Posts;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookmarkResponse {
    PostResponse postResponse;
    Date createdTime;
}

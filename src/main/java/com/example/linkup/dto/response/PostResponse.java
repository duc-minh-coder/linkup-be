package com.example.linkup.dto.response;

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

    String content;

    List<PostMediaResponse> postMedia;

    Date createdTime;

    Date updatedTime;
}

package com.example.linkup.dto.response;

import com.example.linkup.enums.MediaType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostMediaResponse {
    int id;
    String url;
    MediaType mediaType;
    short orderIndex;
}

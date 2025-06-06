package com.example.linkup.dto.request;

import com.example.linkup.enums.MediaType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {
    String content;

    MediaType mediaType;

    List<MultipartFile> media;
}

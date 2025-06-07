package com.example.linkup.dto.request;

import com.example.linkup.enums.MediaType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostMediaRequest {
    MediaType mediaType;

    MultipartFile media;
}

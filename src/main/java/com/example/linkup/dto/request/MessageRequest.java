package com.example.linkup.dto.request;

import com.example.linkup.entity.Users;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRequest {
    int receiverId;
    String content;
}

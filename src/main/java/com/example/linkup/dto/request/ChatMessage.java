package com.example.linkup.dto.request;

import com.example.linkup.enums.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    int senderId;
    int receiverId;
    MessageType type;
    String content;
    Date createdTime;
}

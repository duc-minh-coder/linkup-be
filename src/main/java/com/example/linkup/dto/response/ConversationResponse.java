package com.example.linkup.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    int userId;
    String username;
    String userAvatarUrl;
    String lastMessage;
    Date lastMessageTime;
    boolean userSentLast;
    boolean isOnline;
}

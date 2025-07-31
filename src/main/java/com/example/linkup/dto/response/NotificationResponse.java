package com.example.linkup.dto.response;

import com.example.linkup.enums.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    int id;
    int receiverId;
    int actorId;
    String actorName;
    String actorAvt;
    Integer postId;
    Integer commentId;
    NotificationType type;
    Date createdTime;
}

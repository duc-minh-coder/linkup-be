package com.example.linkup.dto.response;

import com.example.linkup.entity.Users;
import com.example.linkup.enums.MessageType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse {
    int id;
    int senderId;
    String senderName;
    int receiverId;
    String receiverName;
    String content;
    Date createdTime;
    boolean isRead;
    MessageType type;
}

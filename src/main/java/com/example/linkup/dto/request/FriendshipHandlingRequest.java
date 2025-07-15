package com.example.linkup.dto.request;

import com.example.linkup.enums.FriendshipStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendshipHandlingRequest {
    int otherUserId;
    FriendshipStatus status;
}

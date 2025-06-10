package com.example.linkup.dto.response;

import com.example.linkup.entity.Users;
import com.example.linkup.entity.keys.KeyFriendships;
import com.example.linkup.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendshipResponse {

    KeyFriendships id;

    Users user;

    Users friend;

    Date createdTime;

    FriendshipStatus status;
}

package com.example.linkup.dto.response;

import com.example.linkup.enums.FriendshipStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    private int id;
    private String fullName;
    private Date timeNameChange;
    private String avatarUrl;
    private String bio;
    private String location;
    private Date birthday;
    private FriendshipStatus friendshipStatus;
    private int countPost;
    private int countFriend;
}

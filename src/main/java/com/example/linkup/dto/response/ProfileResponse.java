package com.example.linkup.dto.response;

import com.example.linkup.enums.RoleType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    private int id;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String location;
    private Date birthday;
    private RoleType role;
    private int countPost;
    private int countFriend;
}

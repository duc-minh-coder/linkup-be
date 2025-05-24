package com.example.linkup.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileWithUserResponse {
    private int user_id;
    private String full_name;
    private String avatar_Url;
    private String location;
    private String bio;
    private Date birthday;
    private String profile_picture;
}

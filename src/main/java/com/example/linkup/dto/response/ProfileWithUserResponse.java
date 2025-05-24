package com.example.linkup.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@Builder
public class ProfileWithUserResponse {
    private int id;
    private String profilePicture;
    private String bio;
    private String avatarUrl;
    private String location;
    private Date birthday;
    private String fullName;

    public ProfileWithUserResponse(
            int id, String profilePicture, String bio,
            String avatarUrl, String location, Date birthday,
            String fullName) {
        this.id = id;
        this.profilePicture = profilePicture;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.location = location;
        this.birthday = birthday;
        this.fullName = fullName;
    }
}

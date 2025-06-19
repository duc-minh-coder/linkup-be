package com.example.linkup.dto.response;

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
    private String avatarUrl;
    private String bio;
    private String location;
    private Date birthday;
}

package com.example.linkup.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Profiles {
    @Id
    @Column(name = "user_id")
    int userId;

    @Column(name = "full_name")
    @NotNull
    String fullName;

    @Column(name = "profile_picture")
    @Builder.Default
    String profilePicture = null;

    @Column(name = "bio")
    @Builder.Default
    String bio = null;

    @Column(name = "avatar_url")
    @Builder.Default
    String avatarUrl = null;

    @Column(name = "location")
    @Builder.Default
    String location = null;

    @Column(name = "birthday")
    @Builder.Default
    Date birthday = null;

    @Column(name = "updated_time")
    @Builder.Default
    Date updatedTime = null;

    @OneToOne
    @JoinColumn(name = "user_id")
    @MapsId
    Users users;
}

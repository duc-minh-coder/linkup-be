package com.example.linkup.entity;

import jakarta.persistence.*;
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
    int user_id;

    @Column(name = "profile_picture")
    @Builder.Default
    String profile_picture = null;

    @Column(name = "bio")
    @Builder.Default
    String bio = null;

    @Column(name = "avatar_url")
    @Builder.Default
    String avatar_url = null;

    @Column(name = "location")
    @Builder.Default
    String location = null;

    @Column(name = "birthday")
    @Builder.Default
    Date birthday = null;

    @Column(name = "updated_time")
    @Builder.Default
    Date updated_time = null;

    @OneToOne
    @JoinColumn(name = "user_id")
    @MapsId
    Users users;
}

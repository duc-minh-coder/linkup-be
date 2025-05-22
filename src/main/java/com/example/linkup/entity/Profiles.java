package com.example.linkup.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Column(name = "full_name")
    @Builder.Default
    String full_name = null;

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
    String birthday = null;

    @Column(name = "updated_time")
    @Builder.Default
    String updated_time = null;

    @OneToOne
    @JoinColumn(name = "user_id")
    @MapsId
    Users users;
}

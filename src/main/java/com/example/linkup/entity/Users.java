package com.example.linkup.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Column(name = "username", unique = true)
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "created_time")
    Date createdTime;

    @Column(name = "updated_time")
    Date updatedTime;

    // profile
    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
    Profiles profile;

    // post
    @OneToMany(mappedBy = "author")
    List<Posts> posts;

    // friendships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Friendships> friendsSent; // ds ng user đã gửi

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL)
    List<Friendships> friendsReceived; // ds ng đã gửi kb cho user

    // PostLike
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<PostLikes> postLikes;

    // comment
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    List<Comments> commentList;

    // message
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Messages> sentMessages;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Messages> receivedMessages;
}

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
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Column(name = "content")
    String content;

    @Column(name = "created_time")
    Date createdTime;

    @Column(name = "updated_time")
    Date updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_post_id")
    Posts originalPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    Users author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PostMedia> postMedia;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    List<PostLikes> likeList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    List<Comments> commentList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    List<Bookmarks> bookmarks;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    List<Notifications> notifications;
}

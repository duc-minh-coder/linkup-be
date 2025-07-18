package com.example.linkup.entity;

import com.example.linkup.entity.keys.KeyBookmarks;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Bookmarks {
    @EmbeddedId
    KeyBookmarks id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    Users user;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    Posts post;

    @Column(name = "created_time")
    Date createdTime;
}

package com.example.linkup.entity;

import com.example.linkup.entity.keys.KeyPostLikes;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostLikes {
    @EmbeddedId
    KeyPostLikes id;

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

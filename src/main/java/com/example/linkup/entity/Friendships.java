package com.example.linkup.entity;

import com.example.linkup.entity.keys.KeyFriendships;
import com.example.linkup.enums.FriendshipStatus;
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
public class Friendships {
    @EmbeddedId
    KeyFriendships id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    Users user;

    @ManyToOne
    @MapsId("friendId")
    @JoinColumn(name = "friend_id")
    Users friend;

    @Column(name = "created_time")
    Date createdTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    FriendshipStatus status;
}

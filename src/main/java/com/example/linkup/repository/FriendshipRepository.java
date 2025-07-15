package com.example.linkup.repository;

import com.example.linkup.entity.Friendships;
import com.example.linkup.entity.keys.KeyFriendships;
import com.example.linkup.enums.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendships, KeyFriendships> {
    List<Friendships> findByIdUserIdAndStatus(int userId, FriendshipStatus status);

//    List<Friendships> findByIdFriendIdAndStatus(int friendId, FriendshipStatus status);
}

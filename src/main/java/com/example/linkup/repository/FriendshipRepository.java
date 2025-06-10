package com.example.linkup.repository;

import com.example.linkup.entity.Friendships;
import com.example.linkup.entity.keys.KeyFriendships;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendships, KeyFriendships> {

}

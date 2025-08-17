package com.example.linkup.repository;

import com.example.linkup.entity.Friendships;
import com.example.linkup.entity.keys.KeyFriendships;
import com.example.linkup.enums.FriendshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendships, KeyFriendships> {
    List<Friendships> findByIdUserIdAndStatus(int userId, FriendshipStatus status);

    @Query("SELECT f FROM Friendships f " +
            "WHERE f.user.id = :userId " +
            "AND f.status = :status")
    Page<Friendships> findFriendByUserIdAndStatusWithPage(int userId, FriendshipStatus status, Pageable pageable);

    @Query("SELECT f FROM Friendships f " +
            "WHERE f.user.id = :userId " +
            "AND LOWER(f.friend.profile.fullName) like LOWER(CONCAT('%', :text, '%'))" +
            "AND f.status = :status")
    Page<Friendships> findFriendByUserIdAndStatusWithPageByText(int userId, FriendshipStatus status, Pageable pageable, String text);
}

package com.example.linkup.repository;

import com.example.linkup.dto.response.ProfileResponse;
import com.example.linkup.entity.Profiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profiles, Integer> {
    @Query("SELECT p FROM Profiles p " +
            "WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Profiles> searchUserProfileByName(String text);

    @Query("SELECT p FROM Profiles p " +
            "WHERE LOWER(p.fullName) like LOWER(CONCAT('%', :text, '%'))")
    Page<Profiles> searchUserProfileByNameWithPage(String text, Pageable pageable);

    @Query("SELECT p FROM Profiles p " +
            "JOIN Users u ON u.id = p.userId " +
            "JOIN Friendships f ON (f.user.id = :userId AND f.friend.id = u.id) " +
            "OR (f.friend.id = :userId AND f.user.id = u.id) " +
            "WHERE f.status = 'FRIEND' " +
            "AND LOWER(p.fullName) like LOWER(CONCAT('%', :text, '%'))")
    Page<Profiles> searchFriendsByNameWithPage(int userId, String text, Pageable pageable);
}

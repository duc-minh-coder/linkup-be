package com.example.linkup.repository;

import com.example.linkup.dto.response.ProfileResponse;
import com.example.linkup.entity.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profiles, Integer> {
    @Query("SELECT p FROM Profiles p " +
            "WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Profiles> searchUserProfileByName(String text);
}

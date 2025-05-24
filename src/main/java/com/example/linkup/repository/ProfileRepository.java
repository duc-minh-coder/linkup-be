package com.example.linkup.repository;

import com.example.linkup.dto.response.ProfileWithUserResponse;
import com.example.linkup.entity.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profiles, Integer> {
    @Query("""
        SELECT new com.example.linkup.dto.response.ProfileWithUserResponse(
            u.id ,p.profilePicture, p.bio, p.avatarUrl, p.location, p.birthday, u.fullName
        )
        FROM Profiles p
        JOIN Users u ON p.userId = u.id
    """)
    List<ProfileWithUserResponse> findAllProfilesWithUserResponse();
}

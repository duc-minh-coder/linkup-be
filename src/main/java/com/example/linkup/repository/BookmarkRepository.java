package com.example.linkup.repository;

import com.example.linkup.entity.Bookmarks;
import com.example.linkup.entity.keys.KeyBookmarks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmarks, KeyBookmarks> {

    @Query("SELECT b FROM Bookmarks b " +
            "WHERE b.user.id = :userId " +
            "ORDER BY b.createdTime DESC")
    Page<Bookmarks> findByUserId(int userId, Pageable pageable);

}

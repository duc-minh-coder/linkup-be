package com.example.linkup.repository;

import com.example.linkup.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Posts, Integer> {
    List<Posts> findAllByAuthorId(int authorId);

    @Query("SELECT p FROM Posts p " +
            "WHERE p.author.id = :authorId " +
            "ORDER BY p.createdTime DESC")
    List<Posts> getAllByAuthorId(@Param("authorId") Integer authorId);
}

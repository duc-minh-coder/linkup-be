package com.example.linkup.repository;

import com.example.linkup.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT p FROM Posts p WHERE " +
            "p.author.id IN :userIds")
    Page<Posts> findPostByUserIds(List<Integer> userIds, Pageable pageable);

    @Query("SELECT p FROM Posts p WHERE " +
            "p.author.id = :userIds " +
            "ORDER BY p.createdTime DESC")
    Page<Posts> findPostByUserId(Integer userIds, Pageable pageable);
}

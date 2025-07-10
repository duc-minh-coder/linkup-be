package com.example.linkup.repository;

import com.example.linkup.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Integer> {
    @Query("SELECT c FROM Comments c " +
            "WHERE c.post.id = :postId " +
            "ORDER BY c.updatedTime DESC")
    List<Comments> findAllByPostId(@Param("postId") int postId);
}

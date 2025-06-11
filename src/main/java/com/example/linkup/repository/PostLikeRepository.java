package com.example.linkup.repository;

import com.example.linkup.entity.PostLikes;
import com.example.linkup.entity.keys.KeyPostLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLikes, KeyPostLikes> {
    List<PostLikes> findAllByPostId(int postId);
}

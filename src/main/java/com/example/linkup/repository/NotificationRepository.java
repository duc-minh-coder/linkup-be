package com.example.linkup.repository;

import com.example.linkup.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notifications, Integer> {
    @Query("SELECT n FROM Notifications n WHERE " +
            "n.user.id = :userId ORDER BY " +
            "n.createdTime DESC")
    List<Notifications> findByUserId(int userId);

    @Query("SELECT n FROM Notifications n WHERE " +
            "n.user.id = :userId AND " +
            "n.isRead = false")
    int countUnreadNotification(int userId);

    @Modifying
    @Query("UPDATE  Notifications n SET " +
            "n.isRead = false WHERE " +
            "n.user.id = :userId"
            )
    int setReadNotification(int userId);

    @Query("SELECT COUNT(n) > 0 FROM Notifications n WHERE " +
            "n.actor.id = :actorId AND n.post.id = :postId")
    boolean checkLike(int actorId, int postId);
}

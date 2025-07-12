package com.example.linkup.repository;

import com.example.linkup.entity.Messages;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Messages, Integer> {
    @Query("SELECT m FROM Messages m WHERE m.id IN (" +
            "SELECT MAX(m2.id) FROM Messages m2 WHERE " +
            "(m2.sender.id = :userId OR m2.receiver.id = :userId) " +
            "GROUP BY CASE " +
            "WHEN m2.sender.id = :userId THEN m2.receiver.id " +
            "ELSE m2.sender.id END)")
    List<Messages> findLastMessageWithEachUser(int userId);

    // tìm cuọc trò chuyện giữa 2 user
    @Query("SELECT m FROM Messages m WHERE " +
            "(m.sender.id = :userId AND m.receiver.id = :otherUserId) OR " +
            "(m.sender.id = :otherUserId AND m.receiver.id = :userId) " +
            "ORDER BY m.createdTime ASC")
    List<Messages> findConversationBetweenUsers(int userId, int otherUserId);

    @Query("SELECT m FROM Messages m WHERE m.id = ( " +
            "SELECT MAX(m2.id) FROM Messages m2 WHERE " +
            "((m.sender.id = :userId AND m.receiver.id = :otherUserId) OR " +
            "(m.sender.id = :otherUserId AND m.receiver.id = :userId)) " +
            ")")
    Messages findLastMessageBetweenUsers(int userId, int otherUserId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Messages m WHERE " +
            "(m.sender.id = :userId AND m.receiver.id = :otherUserId) OR " +
            "(m.receiver.id = :userId AND m.sender.id = :otherUserId)")
    void deleteConversation(int userId, int otherUserId);

    // lấy ra tn theo trang
    @Query("SELECT m FROM Messages m WHERE " +
            "(m.sender.id = :userId AND m.receiver.id = :otherUserId) OR " +
            "(m.sender.id = :otherUserId AND m.receiver.id = :userId)" +
            "ORDER BY m.createdTime DESC")
    Page<Messages> findConversationBetweenUserWithPaging(int userId, int otherUserId, Pageable pageable);

    // đếm số tin nhắn chưa đọc
    @Query("SELECT COUNT(m) FROM Messages m WHERE " +
            "m.receiver.id = :otherUserId AND m.isRead = false")
    long countUnreadMessages(int otherUserId);
}

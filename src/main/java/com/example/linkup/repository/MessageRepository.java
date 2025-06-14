package com.example.linkup.repository;

import com.example.linkup.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
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


}

package com.blogsphere.repository;

import com.blogsphere.entity.Post;
import com.blogsphere.entity.ReadingHistory;
import com.blogsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {
    Optional<ReadingHistory> findByUserAndPost(User user, Post post);
    List<ReadingHistory> findByUserOrderByViewedAtDesc(User user);
    long countByUser(User user);
    void deleteByUser(User user);
}

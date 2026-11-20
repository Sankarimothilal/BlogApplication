package com.blogsphere.repository;

import com.blogsphere.entity.Bookmark;
import com.blogsphere.entity.Post;
import com.blogsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    List<Bookmark> findByUserOrderByCreatedAtDesc(User user);
    long countByUser(User user);
}

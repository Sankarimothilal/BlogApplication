package com.blogsphere.repository;

import com.blogsphere.entity.Like;
import com.blogsphere.entity.Post;
import com.blogsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    long countByPost(Post post);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.author = :author")
    long countLikesReceivedByAuthor(@Param("author") User author);
}

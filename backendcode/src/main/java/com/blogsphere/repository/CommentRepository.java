package com.blogsphere.repository;

import com.blogsphere.entity.Comment;
import com.blogsphere.entity.Post;
import com.blogsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);
    List<Comment> findByPostAndParentIsNullOrderByCreatedAtDesc(Post post);
    List<Comment> findAllByOrderByCreatedAtDesc();
    long countByPost(Post post);
    long countByUser(User user);
}

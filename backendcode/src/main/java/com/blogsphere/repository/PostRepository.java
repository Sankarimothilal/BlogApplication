package com.blogsphere.repository;

import com.blogsphere.entity.Category;
import com.blogsphere.entity.Post;
import com.blogsphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);

    Page<Post> findByStatus(String status, Pageable pageable);

    Page<Post> findByStatusAndCategory(String status, Category category, Pageable pageable);

    Page<Post> findByAuthor(User author, Pageable pageable);

    Page<Post> findByAuthorAndStatus(User author, String status, Pageable pageable);

    long countByAuthorAndStatus(User author, String status);

    @Query("SELECT COALESCE(SUM(p.viewCount), 0) FROM Post p WHERE p.author = :author")
    Long sumViewsByAuthor(@Param("author") User author);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.summary) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.author.fullName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Post> searchPosts(@Param("query") String query, Pageable pageable);

    List<Post> findTop5ByStatusOrderByViewCountDesc(String status);

    List<Post> findTop6ByStatusOrderByCreatedAtDesc(String status);

    long countByStatus(String status);
}

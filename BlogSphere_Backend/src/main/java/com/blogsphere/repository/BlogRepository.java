package com.blogsphere.repository;
import com.blogsphere.entity.*; 
import org.springframework.data.jpa.repository.JpaRepository; 
import java.util.List; 

public interface BlogRepository extends JpaRepository<Blog,Long>{ List<Blog> findByStatusOrderByCreatedAtDesc(BlogStatus status); List<Blog> findByAuthorIdOrderByCreatedAtDesc(Long authorId); List<Blog> findByStatusAndCategoryIgnoreCaseOrderByCreatedAtDesc(BlogStatus status,String category); }
package com.blogsphere.controller;

import com.blogsphere.dto.request.CreatePostRequest;
import com.blogsphere.dto.response.ApiResponse;
import com.blogsphere.dto.response.PostDetailDTO;
import com.blogsphere.dto.response.PostSummaryDTO;
import com.blogsphere.entity.User;
import com.blogsphere.service.AuthService;
import com.blogsphere.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostSummaryDTO>>> getAllPosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostSummaryDTO> posts = postService.getAllPublishedPosts(search, category, page, size);
        return ResponseEntity.ok(ApiResponse.ok(posts));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<PostSummaryDTO>>> getFeaturedPosts() {
        List<PostSummaryDTO> posts = postService.getFeaturedPosts();
        return ResponseEntity.ok(ApiResponse.ok(posts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDetailDTO>> getPostById(@PathVariable Long id) {
        PostDetailDTO post = postService.getPostDetails(id);
        return ResponseEntity.ok(ApiResponse.ok(post));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PostDetailDTO>> createPost(@Valid @RequestBody CreatePostRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        PostDetailDTO created = postService.createPost(request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Post created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PostDetailDTO>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody CreatePostRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        PostDetailDTO updated = postService.updatePost(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Post updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        postService.deletePost(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Post deleted successfully", null));
    }
}

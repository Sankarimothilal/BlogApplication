package com.blogsphere.controller;

import com.blogsphere.dto.request.CreateCommentRequest;
import com.blogsphere.dto.response.ApiResponse;
import com.blogsphere.dto.response.CommentDTO;
import com.blogsphere.entity.User;
import com.blogsphere.service.AuthService;
import com.blogsphere.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private AuthService authService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getComments(@PathVariable Long postId) {
        List<CommentDTO> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(ApiResponse.ok(comments));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentDTO>> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        CommentDTO comment = commentService.addComment(postId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Comment added", comment));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        commentService.deleteComment(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Comment deleted", null));
    }
}

package com.blogsphere.controller;

import com.blogsphere.dto.request.ReportPostRequest;
import com.blogsphere.dto.response.ApiResponse;
import com.blogsphere.dto.response.PostSummaryDTO;
import com.blogsphere.entity.User;
import com.blogsphere.service.AuthService;
import com.blogsphere.service.InteractionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    @Autowired
    private AuthService authService;

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Map<String, Object> result = interactionService.toggleLike(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/posts/{id}/bookmark")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleBookmark(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Map<String, Object> result = interactionService.toggleBookmark(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/users/{authorId}/follow")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleFollow(@PathVariable Long authorId) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Map<String, Object> result = interactionService.toggleFollow(authorId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/users/bookmarks")
    public ResponseEntity<ApiResponse<List<PostSummaryDTO>>> getBookmarks() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok(interactionService.getUserBookmarks(currentUser)));
    }

    @GetMapping("/users/history")
    public ResponseEntity<ApiResponse<List<PostSummaryDTO>>> getReadingHistory() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok(interactionService.getUserReadingHistory(currentUser)));
    }

    @PostMapping("/posts/{id}/report")
    public ResponseEntity<ApiResponse<Void>> reportPost(
            @PathVariable Long id,
            @Valid @RequestBody ReportPostRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        interactionService.reportPost(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Post reported to admin", null));
    }
}

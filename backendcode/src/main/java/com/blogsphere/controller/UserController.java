package com.blogsphere.controller;

import com.blogsphere.dto.request.ChangePasswordRequest;
import com.blogsphere.dto.request.UpdateProfileRequest;
import com.blogsphere.dto.response.*;
import com.blogsphere.entity.Notification;
import com.blogsphere.entity.User;
import com.blogsphere.exception.ResourceNotFoundException;
import com.blogsphere.repository.UserRepository;
import com.blogsphere.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<UserDashboardStatsDTO>> getUserDashboard() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getUserDashboard(currentUser)));
    }

    @GetMapping("/reader-dashboard")
    public ResponseEntity<ApiResponse<ReaderDashboardDTO>> getReaderDashboard() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getReaderDashboard(currentUser)));
    }

    @GetMapping("/authors")
    public ResponseEntity<ApiResponse<List<UserSummaryDTO>>> getAuthors() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getAllAuthors(currentUser)));
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<AuthorProfileDTO>> getAuthorProfile(@PathVariable Long id) {
        User author = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getAuthorProfile(author, currentUser)));
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<ApiResponse<Page<PostSummaryDTO>>> getAuthorPosts(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User author = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        return ResponseEntity.ok(ApiResponse.ok(postService.getPostsByAuthor(author, status, page, size)));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> updateProfile(@RequestBody UpdateProfileRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        User updated = dashboardService.updateProfile(currentUser, request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", dashboardService.mapToUserSummary(updated, updated)));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        dashboardService.changePassword(currentUser, request);
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully", null));
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getUserNotifications(currentUser)));
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markNotificationRead(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        notificationService.markAsRead(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Marked as read", null));
    }

    @PutMapping("/notifications/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllNotificationsRead() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok(ApiResponse.ok("All marked as read", null));
    }
}

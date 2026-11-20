package com.blogsphere.controller;

import com.blogsphere.dto.response.AdminStatsDTO;
import com.blogsphere.dto.response.ApiResponse;
import com.blogsphere.dto.response.CommentDTO;
import com.blogsphere.dto.response.UserSummaryDTO;
import com.blogsphere.entity.Report;
import com.blogsphere.entity.User;
import com.blogsphere.exception.ResourceNotFoundException;
import com.blogsphere.repository.ReportRepository;
import com.blogsphere.service.AuthService;
import com.blogsphere.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private AuthService authService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminStatsDTO>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getAdminDashboard()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserSummaryDTO>>> getAllUsers() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getAllUserSummaries(currentUser)));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String role = body.get("role");
        dashboardService.updateUserRole(id, role);
        return ResponseEntity.ok(ApiResponse.ok("User role updated successfully", null));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        dashboardService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted successfully", null));
    }

    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getAllComments() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getAllCommentsForAdmin()));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        dashboardService.deleteComment(id);
        return ResponseEntity.ok(ApiResponse.ok("Comment deleted successfully", null));
    }

    @PutMapping("/reports/{id}")
    public ResponseEntity<ApiResponse<Void>> updateReportStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "REVIEWED");
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setStatus(status.toUpperCase());
        reportRepository.save(report);
        return ResponseEntity.ok(ApiResponse.ok("Report updated to " + status, null));
    }
}

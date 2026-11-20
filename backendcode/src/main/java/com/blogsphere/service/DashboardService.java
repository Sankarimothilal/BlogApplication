package com.blogsphere.service;

import com.blogsphere.dto.request.ChangePasswordRequest;
import com.blogsphere.dto.request.UpdateProfileRequest;
import com.blogsphere.dto.response.*;
import com.blogsphere.entity.*;
import com.blogsphere.exception.ResourceNotFoundException;
import com.blogsphere.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ReadingHistoryRepository readingHistoryRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDashboardStatsDTO getUserDashboard(User user) {
        long published = postRepository.countByAuthorAndStatus(user, "PUBLISHED");
        long drafts = postRepository.countByAuthorAndStatus(user, "DRAFT");
        Long totalViews = postRepository.sumViewsByAuthor(user);
        if (totalViews == null) totalViews = 0L;
        long totalLikes = likeRepository.countLikesReceivedByAuthor(user);

        // Fetch recent notifications to format as Recent Activity
        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        List<UserDashboardStatsDTO.ActivityItemDTO> activity = new ArrayList<>();

        for (Notification n : notifications.stream().limit(5).collect(Collectors.toList())) {
            String timeAgo = "recently";
            activity.add(new UserDashboardStatsDTO.ActivityItemDTO(n.getType(), n.getMessage(), timeAgo));
        }

        if (activity.isEmpty()) {
            activity.add(new UserDashboardStatsDTO.ActivityItemDTO("INFO", "Welcome to BlogSphere! Start by writing your first article.", "Just now"));
        }

        return new UserDashboardStatsDTO(published, drafts, totalViews, totalLikes, activity);
    }

    public ReaderDashboardDTO getReaderDashboard(User user) {
        long readCount = readingHistoryRepository.countByUser(user);
        long bookmarkCount = bookmarkRepository.countByUser(user);
        long followingCount = followRepository.countByFollower(user);
        long commentsCount = commentRepository.countByUser(user);

        List<PostSummaryDTO> saved = bookmarkRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(Bookmark::getPost)
                .map(postService::mapToSummaryDTO)
                .collect(Collectors.toList());

        List<PostSummaryDTO> history = readingHistoryRepository.findByUserOrderByViewedAtDesc(user).stream()
                .map(ReadingHistory::getPost)
                .map(postService::mapToSummaryDTO)
                .collect(Collectors.toList());

        List<UserSummaryDTO> followedAuthors = followRepository.findByFollower(user).stream()
                .map(Follow::getFollowing)
                .map(author -> mapToUserSummary(author, user))
                .collect(Collectors.toList());

        return new ReaderDashboardDTO(readCount, bookmarkCount, followingCount, commentsCount, saved, history, followedAuthors);
    }

    public List<UserSummaryDTO> getAllAuthors(User currentUser) {
        List<User> authors = userRepository.findByRole(Role.ROLE_AUTHOR);
        return authors.stream().map(a -> mapToUserSummary(a, currentUser)).collect(Collectors.toList());
    }

    public List<UserSummaryDTO> getAllUserSummaries(User currentUser) {
        return userRepository.findAll().stream()
                .map(u -> mapToUserSummary(u, currentUser))
                .collect(Collectors.toList());
    }

    public List<CommentDTO> getAllCommentsForAdmin() {
        return commentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(c -> new CommentDTO(
                    c.getId(),
                    c.getPost().getId(),
                    c.getUser().getId(),
                    c.getUser().getFullName(),
                    c.getUser().getAvatarUrl(),
                    c.getContent(),
                    c.getParent() != null ? c.getParent().getId() : null,
                    c.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void updateUserRole(Long userId, String roleStr) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role newRole = Role.valueOf(roleStr);
        u.setRole(newRole);
        userRepository.save(u);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public User updateProfile(User user, UpdateProfileRequest request) {
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isBlank()) {
            user.setAvatarUrl(request.getAvatarUrl().trim());
        }
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password does not match");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public AdminStatsDTO getAdminDashboard() {
        long totalUsers = userRepository.count();
        long totalBlogs = postRepository.count();
        long reportedCount = reportRepository.count();
        long pendingReportsCount = reportRepository.countByStatus("PENDING");

        List<Report> reports = reportRepository.findAllByOrderByCreatedAtDesc();
        List<AdminStatsDTO.ReportItemDTO> reportDTOs = reports.stream().limit(10).map(r -> new AdminStatsDTO.ReportItemDTO(
            r.getId(),
            r.getPost().getId(),
            r.getPost().getTitle(),
            r.getReportedBy().getFullName(),
            r.getReason(),
            r.getStatus()
        )).collect(Collectors.toList());

        return new AdminStatsDTO(totalUsers, totalBlogs, reportedCount, pendingReportsCount, reportDTOs);
    }

    public AuthorProfileDTO getAuthorProfile(User author, User currentUser) {
        long totalBlogs = postRepository.countByAuthorAndStatus(author, "PUBLISHED");
        long followersCount = followRepository.countByFollowing(author);
        long followingCount = followRepository.countByFollower(author);

        boolean followedByCurrentUser = false;
        if (currentUser != null && !currentUser.getId().equals(author.getId())) {
            followedByCurrentUser = followRepository.existsByFollowerAndFollowing(currentUser, author);
        }

        AuthorProfileDTO dto = new AuthorProfileDTO();
        dto.setId(author.getId());
        dto.setFullName(author.getFullName());
        dto.setRole(author.getRole().name().replace("ROLE_", ""));
        dto.setBio(author.getBio() != null ? author.getBio() : "Author at BlogSphere.");
        dto.setAvatarUrl(author.getAvatarUrl());
        dto.setTotalBlogs(totalBlogs);
        dto.setFollowersCount(followersCount);
        dto.setFollowingCount(followingCount);
        dto.setFollowedByCurrentUser(followedByCurrentUser);
        return dto;
    }

    public UserSummaryDTO mapToUserSummary(User u, User currentUser) {
        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole().name());
        dto.setBio(u.getBio());
        dto.setAvatarUrl(u.getAvatarUrl());
        dto.setTotalBlogs(postRepository.countByAuthorAndStatus(u, "PUBLISHED"));
        dto.setFollowersCount(followRepository.countByFollowing(u));
        dto.setCreatedAt(u.getCreatedAt());

        if (currentUser != null && !currentUser.getId().equals(u.getId())) {
            dto.setFollowedByCurrentUser(followRepository.existsByFollowerAndFollowing(currentUser, u));
        } else {
            dto.setFollowedByCurrentUser(false);
        }

        return dto;
    }
}

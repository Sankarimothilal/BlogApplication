package com.blogsphere.service;

import com.blogsphere.dto.request.ReportPostRequest;
import com.blogsphere.dto.response.PostSummaryDTO;
import com.blogsphere.entity.*;
import com.blogsphere.exception.ResourceNotFoundException;
import com.blogsphere.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InteractionService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReadingHistoryRepository readingHistoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Map<String, Object> toggleLike(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Optional<Like> existing = likeRepository.findByUserAndPost(user, post);
        boolean isLiked;
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            isLiked = false;
        } else {
            likeRepository.save(new Like(user, post));
            isLiked = true;

            // Notify author
            if (!post.getAuthor().getId().equals(user.getId())) {
                notificationService.createNotification(
                    post.getAuthor(),
                    user,
                    "LIKE",
                    user.getFullName() + " liked your blog \"" + post.getTitle() + "\".",
                    "/blog-detail.html?id=" + post.getId()
                );
            }
        }

        long count = likeRepository.countByPost(post);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", isLiked);
        result.put("likeCount", count);
        return result;
    }

    @Transactional
    public Map<String, Object> toggleBookmark(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Optional<Bookmark> existing = bookmarkRepository.findByUserAndPost(user, post);
        boolean isBookmarked;
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            isBookmarked = false;
        } else {
            bookmarkRepository.save(new Bookmark(user, post));
            isBookmarked = true;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bookmarked", isBookmarked);
        return result;
    }

    @Transactional
    public Map<String, Object> toggleFollow(Long authorId, User user) {
        if (authorId.equals(user.getId())) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        Optional<Follow> existing = followRepository.findByFollowerAndFollowing(user, author);
        boolean isFollowing;
        if (existing.isPresent()) {
            followRepository.delete(existing.get());
            isFollowing = false;
        } else {
            followRepository.save(new Follow(user, author));
            isFollowing = true;

            // Notify author
            notificationService.createNotification(
                author,
                user,
                "FOLLOW",
                user.getFullName() + " started following you.",
                "/author-profile.html?id=" + user.getId()
            );
        }

        long followersCount = followRepository.countByFollowing(author);
        Map<String, Object> result = new HashMap<>();
        result.put("following", isFollowing);
        result.put("followersCount", followersCount);
        return result;
    }

    public List<PostSummaryDTO> getUserBookmarks(User user) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserOrderByCreatedAtDesc(user);
        return bookmarks.stream()
                .map(Bookmark::getPost)
                .map(postService::mapToSummaryDTO)
                .collect(Collectors.toList());
    }

    public List<PostSummaryDTO> getUserReadingHistory(User user) {
        List<ReadingHistory> history = readingHistoryRepository.findByUserOrderByViewedAtDesc(user);
        return history.stream()
                .map(ReadingHistory::getPost)
                .map(postService::mapToSummaryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void reportPost(Long postId, ReportPostRequest request, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Report report = new Report(post, user, request.getReason());
        reportRepository.save(report);
    }
}

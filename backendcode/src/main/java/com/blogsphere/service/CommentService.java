package com.blogsphere.service;

import com.blogsphere.dto.request.CreateCommentRequest;
import com.blogsphere.dto.response.CommentDTO;
import com.blogsphere.entity.Comment;
import com.blogsphere.entity.Post;
import com.blogsphere.entity.Role;
import com.blogsphere.entity.User;
import com.blogsphere.exception.ResourceNotFoundException;
import com.blogsphere.repository.CommentRepository;
import com.blogsphere.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public CommentDTO addComment(Long postId, CreateCommentRequest request, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId()).orElse(null);
        }

        Comment comment = new Comment(post, user, parent, request.getContent());
        Comment saved = commentRepository.save(comment);

        // Notify post author if commenter is not author
        if (!post.getAuthor().getId().equals(user.getId())) {
            notificationService.createNotification(
                post.getAuthor(),
                user,
                "COMMENT",
                user.getFullName() + " commented on your blog \"" + post.getTitle() + "\".",
                "/blog-detail.html?id=" + post.getId()
            );
        }

        return mapToDTO(saved);
    }

    public List<CommentDTO> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return commentRepository.findByPostOrderByCreatedAtDesc(post)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new SecurityException("Permission denied");
        }

        commentRepository.delete(comment);
    }

    private CommentDTO mapToDTO(Comment comment) {
        return new CommentDTO(
            comment.getId(),
            comment.getPost().getId(),
            comment.getUser().getId(),
            comment.getUser().getFullName(),
            comment.getUser().getAvatarUrl(),
            comment.getContent(),
            comment.getParent() != null ? comment.getParent().getId() : null,
            comment.getCreatedAt()
        );
    }
}

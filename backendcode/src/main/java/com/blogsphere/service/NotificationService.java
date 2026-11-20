package com.blogsphere.service;

import com.blogsphere.entity.Follow;
import com.blogsphere.entity.Notification;
import com.blogsphere.entity.Post;
import com.blogsphere.entity.User;
import com.blogsphere.repository.FollowRepository;
import com.blogsphere.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FollowRepository followRepository;

    @Transactional
    public void createNotification(User recipient, User actor, String type, String message, String targetUrl) {
        if (recipient.getId().equals(actor.getId())) {
            return; // Don't notify oneself
        }
        Notification notification = new Notification(recipient, actor, type, message, targetUrl);
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyFollowersNewPost(User author, Post post) {
        List<Follow> followers = followRepository.findByFollowing(author);
        for (Follow follow : followers) {
            createNotification(
                follow.getFollower(),
                author,
                "NEW_POST",
                "A new blog \"" + post.getTitle() + "\" was published by " + author.getFullName() + ".",
                "/blog-detail.html?id=" + post.getId()
            );
        }
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId, User user) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getRecipient().getId().equals(user.getId())) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        });
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> list = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        for (Notification n : list) {
            n.setRead(true);
        }
        notificationRepository.saveAll(list);
    }
}

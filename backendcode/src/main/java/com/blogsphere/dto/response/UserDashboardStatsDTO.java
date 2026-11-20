package com.blogsphere.dto.response;

import java.util.List;

public class UserDashboardStatsDTO {

    private long publishedBlogs;
    private long drafts;
    private long totalViews;
    private long totalLikes;
    private List<ActivityItemDTO> recentActivity;

    public UserDashboardStatsDTO() {}

    public UserDashboardStatsDTO(long publishedBlogs, long drafts, long totalViews, long totalLikes, List<ActivityItemDTO> recentActivity) {
        this.publishedBlogs = publishedBlogs;
        this.drafts = drafts;
        this.totalViews = totalViews;
        this.totalLikes = totalLikes;
        this.recentActivity = recentActivity;
    }

    public long getPublishedBlogs() {
        return publishedBlogs;
    }

    public void setPublishedBlogs(long publishedBlogs) {
        this.publishedBlogs = publishedBlogs;
    }

    public long getDrafts() {
        return drafts;
    }

    public void setDrafts(long drafts) {
        this.drafts = drafts;
    }

    public long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(long totalViews) {
        this.totalViews = totalViews;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public List<ActivityItemDTO> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<ActivityItemDTO> recentActivity) {
        this.recentActivity = recentActivity;
    }

    public static class ActivityItemDTO {
        private String type; // LIKE, COMMENT, PUBLISH
        private String text;
        private String timeAgo;

        public ActivityItemDTO() {}

        public ActivityItemDTO(String type, String text, String timeAgo) {
            this.type = type;
            this.text = text;
            this.timeAgo = timeAgo;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTimeAgo() {
            return timeAgo;
        }

        public void setTimeAgo(String timeAgo) {
            this.timeAgo = timeAgo;
        }
    }
}

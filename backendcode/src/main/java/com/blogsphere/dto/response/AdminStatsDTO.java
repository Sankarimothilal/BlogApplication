package com.blogsphere.dto.response;

import java.util.List;

public class AdminStatsDTO {

    private long totalUsers;
    private long totalBlogs;
    private long reportedCount;
    private long pendingReportsCount;
    private List<ReportItemDTO> recentReports;

    public AdminStatsDTO() {}

    public AdminStatsDTO(long totalUsers, long totalBlogs, long reportedCount, long pendingReportsCount, List<ReportItemDTO> recentReports) {
        this.totalUsers = totalUsers;
        this.totalBlogs = totalBlogs;
        this.reportedCount = reportedCount;
        this.pendingReportsCount = pendingReportsCount;
        this.recentReports = recentReports;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalBlogs() {
        return totalBlogs;
    }

    public void setTotalBlogs(long totalBlogs) {
        this.totalBlogs = totalBlogs;
    }

    public long getReportedCount() {
        return reportedCount;
    }

    public void setReportedCount(long reportedCount) {
        this.reportedCount = reportedCount;
    }

    public long getPendingReportsCount() {
        return pendingReportsCount;
    }

    public void setPendingReportsCount(long pendingReportsCount) {
        this.pendingReportsCount = pendingReportsCount;
    }

    public List<ReportItemDTO> getRecentReports() {
        return recentReports;
    }

    public void setRecentReports(List<ReportItemDTO> recentReports) {
        this.recentReports = recentReports;
    }

    public static class ReportItemDTO {
        private Long id;
        private Long postId;
        private String blogTitle;
        private String reportedBy;
        private String reason;
        private String status;

        public ReportItemDTO() {}

        public ReportItemDTO(Long id, Long postId, String blogTitle, String reportedBy, String reason, String status) {
            this.id = id;
            this.postId = postId;
            this.blogTitle = blogTitle;
            this.reportedBy = reportedBy;
            this.reason = reason;
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getPostId() {
            return postId;
        }

        public void setPostId(Long postId) {
            this.postId = postId;
        }

        public String getBlogTitle() {
            return blogTitle;
        }

        public void setBlogTitle(String blogTitle) {
            this.blogTitle = blogTitle;
        }

        public String getReportedBy() {
            return reportedBy;
        }

        public void setReportedBy(String reportedBy) {
            this.reportedBy = reportedBy;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}

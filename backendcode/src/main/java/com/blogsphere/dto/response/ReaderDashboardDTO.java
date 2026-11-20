package com.blogsphere.dto.response;

import java.util.List;

public class ReaderDashboardDTO {

    private long articlesReadCount;
    private long bookmarksCount;
    private long followingAuthorsCount;
    private long commentsCount;

    private List<PostSummaryDTO> savedArticles;
    private List<PostSummaryDTO> readingHistory;
    private List<UserSummaryDTO> followedAuthors;

    public ReaderDashboardDTO() {}

    public ReaderDashboardDTO(long articlesReadCount, long bookmarksCount, long followingAuthorsCount, long commentsCount,
                              List<PostSummaryDTO> savedArticles, List<PostSummaryDTO> readingHistory, List<UserSummaryDTO> followedAuthors) {
        this.articlesReadCount = articlesReadCount;
        this.bookmarksCount = bookmarksCount;
        this.followingAuthorsCount = followingAuthorsCount;
        this.commentsCount = commentsCount;
        this.savedArticles = savedArticles;
        this.readingHistory = readingHistory;
        this.followedAuthors = followedAuthors;
    }

    public long getArticlesReadCount() {
        return articlesReadCount;
    }

    public void setArticlesReadCount(long articlesReadCount) {
        this.articlesReadCount = articlesReadCount;
    }

    public long getBookmarksCount() {
        return bookmarksCount;
    }

    public void setBookmarksCount(long bookmarksCount) {
        this.bookmarksCount = bookmarksCount;
    }

    public long getFollowingAuthorsCount() {
        return followingAuthorsCount;
    }

    public void setFollowingAuthorsCount(long followingAuthorsCount) {
        this.followingAuthorsCount = followingAuthorsCount;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public List<PostSummaryDTO> getSavedArticles() {
        return savedArticles;
    }

    public void setSavedArticles(List<PostSummaryDTO> savedArticles) {
        this.savedArticles = savedArticles;
    }

    public List<PostSummaryDTO> getReadingHistory() {
        return readingHistory;
    }

    public void setReadingHistory(List<PostSummaryDTO> readingHistory) {
        this.readingHistory = readingHistory;
    }

    public List<UserSummaryDTO> getFollowedAuthors() {
        return followedAuthors;
    }

    public void setFollowedAuthors(List<UserSummaryDTO> followedAuthors) {
        this.followedAuthors = followedAuthors;
    }
}

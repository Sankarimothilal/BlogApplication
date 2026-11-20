package com.blogsphere.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class CreatePostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String category; // category name or id

    private List<String> tags; // e.g. ["java", "springboot", "web"]

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private String summary;

    private String coverImage;

    private String status = "PUBLISHED"; // "DRAFT" or "PUBLISHED"

    public CreatePostRequest() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

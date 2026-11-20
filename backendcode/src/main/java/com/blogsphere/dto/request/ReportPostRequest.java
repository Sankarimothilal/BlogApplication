package com.blogsphere.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ReportPostRequest {

    @NotBlank(message = "Reason is required")
    private String reason;

    public ReportPostRequest() {}

    public ReportPostRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

package com.example.shareapp.models;

import java.util.UUID;

public class Report {
    private String reportId;
    private String reporterId;
    private String targetId;
    private String postId;
    private String type;
    private String description;

    public Report() {
    }

    public Report(String reportId, String reporterId, String targetId, String postId, String type, String description) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.targetId = targetId;
        this.postId = postId;
        this.type = type;
        this.description = description;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

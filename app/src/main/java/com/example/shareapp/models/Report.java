package com.example.shareapp.models;

import java.util.UUID;

public class Report {
    private UUID reportId;
    private UUID reporterId;
    private UUID targetId;
    private UUID postId;
    private String type;
    private String description;

    public Report() {
    }

    public Report(UUID reportId, UUID reporterId, UUID targetId, UUID postId, String type, String description) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.targetId = targetId;
        this.postId = postId;
        this.type = type;
        this.description = description;
    }

    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }

    public UUID getReporterId() {
        return reporterId;
    }

    public void setReporterId(UUID reporterId) {
        this.reporterId = reporterId;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
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

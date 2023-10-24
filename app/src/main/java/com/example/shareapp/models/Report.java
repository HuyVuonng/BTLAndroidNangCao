package com.example.shareapp.models;

import java.util.UUID;

public class Report {
    private String reportId;
    private User reporter;
    private User target;
    private Post post;
    private String type;
    private String description;

    public Report() {
    }

    public Report(String reportId, User reporter, User target, Post post, String type, String description) {
        this.reportId = reportId;
        this.reporter = reporter;
        this.target = target;
        this.post = post;
        this.type = type;
        this.description = description;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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

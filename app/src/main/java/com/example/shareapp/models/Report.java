package com.example.shareapp.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class Report {
    private String reportId;
    private String reporterId;
    private String targetId;
    private String postId;
    private String type;
    private String description;
    private static DatabaseReference mDatabase;


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

    public Report(String reportId, String reporterId, String targetId, String postId, String type) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.targetId = targetId;
        this.postId = postId;
        this.type = type;
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

    public static void CreateNewReport(String reportId, String reporterId, String targetId, String postId,String type) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Report report = new Report(reportId,reporterId,targetId,postId,type);
        mDatabase.child("Reports").child(reportId).setValue(report);
    }
    public static DatabaseReference getFirebaseReference() {
        return com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Reports");
    }
}

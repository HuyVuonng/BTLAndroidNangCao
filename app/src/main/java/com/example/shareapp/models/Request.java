package com.example.shareapp.models;

import com.google.firebase.database.DatabaseReference;

public class Request {
    private String requestId;
    private String notificationId;
    private String postId;
    private String userId;
    private boolean status;

    public Request() {
    }

    public Request(String requestId, String notificationId, String postId, String userId, boolean status) {
        this.requestId = requestId;
        this.notificationId = notificationId;
        this.postId = postId;
        this.userId = userId;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static DatabaseReference getFirebaseReference() {
        return com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Requests");
    }

    public static void createRequest(Request request) {
        getFirebaseReference().child(request.getRequestId()).setValue(request);
    }
}

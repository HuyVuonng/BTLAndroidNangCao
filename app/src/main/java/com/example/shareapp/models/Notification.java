package com.example.shareapp.models;

import java.util.UUID;

public class Notification {
    private String notifiId;
    private String postId;
    private User userId;
    private String type;

    public Notification() {
    }

    public Notification(String notifiId, String postId, User userId, String type) {
        this.notifiId = notifiId;
        this.postId = postId;
        this.userId = userId;
        this.type = type;
    }

    public String getNotifiId() {
        return notifiId;
    }

    public void setNotifiId(String notifiId) {
        this.notifiId = notifiId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

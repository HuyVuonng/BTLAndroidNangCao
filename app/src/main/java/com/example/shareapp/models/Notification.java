package com.example.shareapp.models;

import java.util.UUID;

public class Notification {
    private UUID notifiId;
    private Post post;
    private String userId;
    private String type;

    public Notification() {
    }

    public Notification(UUID notifiId, Post post, String userId, String type) {
        this.notifiId = notifiId;
        this.post = post;
        this.userId = userId;
        this.type = type;
    }

    public UUID getNotifiId() {
        return notifiId;
    }

    public void setNotifiId(UUID notifiId) {
        this.notifiId = notifiId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

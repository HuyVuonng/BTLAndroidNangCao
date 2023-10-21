package com.example.shareapp.models;

import java.util.UUID;

public class Notification {
    private UUID notifiId;
    private UUID postId;
    private UUID userId;
    private String type;

    public Notification() {
    }

    public Notification(UUID notifiId, UUID postId, UUID userId, String type) {
        this.notifiId = notifiId;
        this.postId = postId;
        this.userId = userId;
        this.type = type;
    }

    public UUID getNotifiId() {
        return notifiId;
    }

    public void setNotifiId(UUID notifiId) {
        this.notifiId = notifiId;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

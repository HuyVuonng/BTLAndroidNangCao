package com.example.shareapp.models;

import java.util.UUID;

public class Notification {
    private String notifiId;
    private Post post;
    private User user;
    private String type;

    public Notification() {
    }

    public Notification(String notifiId, Post post, User user, String type) {
        this.notifiId = notifiId;
        this.post = post;
        this.user = user;
        this.type = type;
    }

    public String getNotifiId() {
        return notifiId;
    }

    public void setNotifiId(String notifiId) {
        this.notifiId = notifiId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

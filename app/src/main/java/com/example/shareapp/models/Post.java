package com.example.shareapp.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class Post implements Serializable {
    private String postId;
    private String userId;
    private String title;
    private String type;
    private int count;
    private String image;
    private String status;
    private long createdAt;
    private long updatedAt;
    private boolean isDelete;
    private String location;
    private String description;
    public Post() {
    }

    public Post(String postId, String userId, String title, String type, int count, String image, String status, long createdAt, long updatedAt, boolean isDelete, String location, String description) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.count = count;
        this.image = image;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDelete = isDelete;
        this.location = location;
        this.description = description;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String convertCreatedAtToDateTime() {
        Date date = new Date(createdAt);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = dateFormat.format(date);

        return formattedDate;
    }

    public String convertUpdatedAtToDateTime() {
        Date date = new Date(updatedAt);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = dateFormat.format(date);

        return formattedDate;
    }
}

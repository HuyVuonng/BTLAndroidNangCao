package com.example.shareapp.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Post implements Serializable {
    private String postId;
    private String userId;
    private String title;
    private String type;
    private int count;
    private long price;
    private String image;
    private String status;
    private long createdAt;
    private long updatedAt;
    private boolean isDelete;
    private Location location;
    private String description;

    public interface IPostDataReceivedListener {
        void onPostDataReceived(Post post);
    }
    public Post() {
    }

    public Post(String postId, String userId, String title, String type, int count, long price, String image, String status, long createdAt, long updatedAt, boolean isDelete, Location location, String description) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.count = count;
        this.price = price;
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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
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

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postId", postId);
        result.put("userId", userId);
        result.put("title", title);
        result.put("type", type);
        result.put("count", count);
        result.put("image", image);
        result.put("status", status);
        result.put("createdAt", createdAt);
        result.put("updatedAt", updatedAt);
        result.put("isDelete", isDelete);
        result.put("location", location);
        result.put("description", description);

        return result;
    }

    public static DatabaseReference getFirebaseReference() {
        return com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Posts");
    }

    public static void hidePost(String postId) {
        getFirebaseReference().child(postId).child("delete").setValue(true);
    }

    public static void showPost(String postId) {
        getFirebaseReference().child(postId).child("delete").setValue(false);
    }

    public static void getPostById(String postId, IPostDataReceivedListener listener) {
        getFirebaseReference().child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Post post = snapshot.getValue(Post.class);
                    listener.onPostDataReceived(post);
                } else {
                    listener.onPostDataReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onPostDataReceived(null);
            }
        });
    }

    public static void reduceCountProduct(String postId) {
        getPostById(postId, new IPostDataReceivedListener() {
            @Override
            public void onPostDataReceived(Post post) {
                if(post != null) {
                    if(post.getCount() > 0) {
                        int count = post.getCount() - 1;
                        getFirebaseReference().child(postId).child("count").setValue(count);

                        if(count <= 0) {
                            hidePost(postId);
                        }
                    }
                }
            }
        });
    }
}

package com.example.shareapp.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class Notification {
    private String notifiId;
    private String targetId;
    private String type;
    private String title;
    private String content;
    private boolean status;
    private long createdAt;

    public interface INotificationReceivedListener {
        void onNotifiDataReceived(Notification notifi);
    }

    public Notification() {
    }

    public Notification(String notifiId, String targetId, String type, String title, String content, boolean status, long createdAt) {
        this.notifiId = notifiId;
        this.targetId = targetId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getNotifiId() {
        return notifiId;
    }

    public void setNotifiId(String notifiId) {
        this.notifiId = notifiId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public static DatabaseReference getFirebaseReference() {
        return com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Notifications");
    }

    public static void createNotification(Notification notifi) {
        getFirebaseReference().child(notifi.getNotifiId()).setValue(notifi);
    }

    public static void getNotificationById(String notifiId, INotificationReceivedListener listener) {
        getFirebaseReference().child(notifiId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Notification notifi = snapshot.getValue(Notification.class);
                    listener.onNotifiDataReceived(notifi);
                } else {
                    listener.onNotifiDataReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onNotifiDataReceived(null);
            }
        });
    }

    public static void readNotification(String notifiId) {
        getFirebaseReference().child(notifiId).child("status").setValue(true);
    }
}

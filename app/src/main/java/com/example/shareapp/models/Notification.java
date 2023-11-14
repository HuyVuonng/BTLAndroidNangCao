package com.example.shareapp.models;

import com.google.firebase.database.DatabaseReference;

import java.util.UUID;

public class Notification {
    private String notifiId;
    private String type;
    private String title;
    private String content;

    public Notification() {
    }

    public Notification(String notifiId, String type, String title, String content) {
        this.notifiId = notifiId;
        this.type = type;
        this.title = title;
        this.content = content;
    }

    public String getNotifiId() {
        return notifiId;
    }

    public void setNotifiId(String notifiId) {
        this.notifiId = notifiId;
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

    public static DatabaseReference getFirebaseReference() {
        return com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Notifications");
    }

    public static void createNotification(Notification notifi) {
        getFirebaseReference().child(notifi.getNotifiId()).setValue(notifi);
    }
}

package com.example.shareapp.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shareapp.controllers.constant.NotifiTypeConstant;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Request {
    private String requestId;
    private String notificationId;
    private String postId;
    private String userId;
    private boolean status;
    private boolean deny;
    private long createdAt;
    private long updatedAt;

    public interface IRequestReceivedListener {
        void onRequestDataReceived(Request request);
    }

    public Request() {
    }

    public Request(String requestId, String notificationId, String postId, String userId, boolean status, boolean deny, long createdAt, long updatedAt) {
        this.requestId = requestId;
        this.notificationId = notificationId;
        this.postId = postId;
        this.userId = userId;
        this.status = status;
        this.deny = deny;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public boolean isDeny() {
        return deny;
    }

    public void setDeny(boolean deny) {
        this.deny = deny;
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

    public static DatabaseReference getFirebaseReference() {
        return com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Requests");
    }

    public static void createRequest(Request request) {
        getFirebaseReference().child(request.getRequestId()).setValue(request);
    }

    public static void getRequestByNotificationId(String notifiId, IRequestReceivedListener listener) {
        getFirebaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFound = false;
                for(DataSnapshot requestSnapshot : snapshot.getChildren()) {
                    Request request = requestSnapshot.getValue(Request.class);
                    if(request.getNotificationId().equals(notifiId)) {
                        listener.onRequestDataReceived(request);
                        isFound = true;
                        break;
                    }
                }
                if(!isFound)
                    listener.onRequestDataReceived(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onRequestDataReceived(null);
            }
        });
    }

    public static void replyRequest(String notificationId, String type) {
        getRequestByNotificationId(notificationId, new IRequestReceivedListener() {
            @Override
            public void onRequestDataReceived(Request request) {
                if(request == null) {
                    return;
                }
                replyRequestById(request.getRequestId(), type);
            }
        });
    }

    public static void getRequestById(String requestId, IRequestReceivedListener listener) {
        getFirebaseReference().child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Request request = snapshot.getValue(Request.class);
                if(request != null) {
                    listener.onRequestDataReceived(request);
                } else {
                    listener.onRequestDataReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onRequestDataReceived(null);
            }
        });
    }

    public static class compareCreatedAtRequest implements Comparator<Request> {
        public int compare(Request a, Request b) {
            if(a.getCreatedAt() > b.getCreatedAt())
                return -1;
            if(a.getCreatedAt() == b.getCreatedAt())
                return 0;
            return 1;
        }
    }

    public static void getRequestLast(String userId, String postId, IRequestReceivedListener listener) {
        getFirebaseReference().orderByChild("createdAt")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for(DataSnapshot rqSnapshot : snapshot.getChildren()) {
                                Request request = rqSnapshot.getValue(Request.class);

                                if(request.getUserId().equals(userId) && request.getPostId().equals(postId)) {
                                    listener.onRequestDataReceived(request);
                                    break;
                                } else {
                                    listener.onRequestDataReceived(null);
                                    break;
                                }
                            }
                        } else {
                            listener.onRequestDataReceived(null);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onRequestDataReceived(null);
                    }
                });
    }

    public static void replyRequestById(String requestId, String type) {
        if(type.equals(NotifiTypeConstant.TYPE_ACCEPT))
            getFirebaseReference().child(requestId).child("status").setValue(true);
        else
            getFirebaseReference().child(requestId).child("deny").setValue(true);
        getFirebaseReference().child(requestId).child("updatedAt").setValue(System.currentTimeMillis());
    }
}

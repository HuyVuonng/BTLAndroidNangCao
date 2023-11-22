package com.example.shareapp.controllers.Services;

import static com.example.shareapp.controllers.application.MyApplication.CHANNEL_ID;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.shareapp.R;
import com.example.shareapp.controllers.activities.MainActivity;
import com.example.shareapp.controllers.activities.NotificationActivity;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.Request;
import com.example.shareapp.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class NotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkNotifiRequest();

        return START_NOT_STICKY;
    }

    private void checkNotifiRequest() {
        com.example.shareapp.models.Notification.getFirebaseReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                com.example.shareapp.models.Notification notifi = snapshot.getValue(com.example.shareapp.models.Notification.class);
                if(notifi == null) {
                    return;
                }

                if(!notifi.isStatus() && notifi.getTargetId().equals(User.getUserInfor(getApplicationContext()).getUid())) {
                    sendNotification(notifi);
                    com.example.shareapp.models.Notification.isReaded(notifi.getNotifiId());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(com.example.shareapp.models.Notification notifi) {
        Intent intent = new Intent(this, NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(notifi.getTitle())
                .setContentText(notifi.getContent())
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.mipmap.ic_launcher, "close", pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(getNotificationId(), notification);
        }
    }
    private int getNotificationId() {
        return (int) new Date().getTime();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

package com.example.shareapp.controllers.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.shareapp.R;
import com.example.shareapp.controllers.adapters.NotificationAdapter;
import com.example.shareapp.controllers.constant.NotifiTypeConstant;
import com.example.shareapp.models.Notification;
import com.example.shareapp.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private ImageButton imbBackPage;
    private TextView tvTitlePage;
    RecyclerView recyclerView;
    private List<Notification> mListNotifi;
    private NotificationAdapter notifiAdapter;

    public NotificationActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        
        getViews();
        setEventListener();
    }

    private void getViews() {
        imbBackPage = findViewById(R.id.imb_back_page);
        tvTitlePage = findViewById(R.id.tv_title_page);
        recyclerView = findViewById(R.id.rcv_list_notifi);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotificationActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        mListNotifi = new ArrayList<>();
        notifiAdapter = new NotificationAdapter(mListNotifi, NotificationActivity.this);
        recyclerView.setAdapter(notifiAdapter);
    }

    private void setEventListener() {
        imbBackPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvTitlePage.setText("Thông báo");
        Query query = Notification.getFirebaseReference().orderByChild("createdAt");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notification notification = snapshot.getValue(Notification.class);
                if(notification == null) {
                    return;
                }
                if(!notification.getTargetId().equals(User.getUserInfor(NotificationActivity.this).getUid())) {
                    return;
                }

                mListNotifi.add(0, notification);

                notifiAdapter.notifyDataSetChanged();
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
}
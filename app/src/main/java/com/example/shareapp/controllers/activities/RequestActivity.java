package com.example.shareapp.controllers.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.shareapp.R;
import com.example.shareapp.controllers.adapters.NotificationAdapter;
import com.example.shareapp.controllers.adapters.RequestAdapter;
import com.example.shareapp.models.Notification;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.Request;
import com.example.shareapp.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends AppCompatActivity {
    private ImageButton imbBackPage;
    private TextView tvTitlePage;
    RecyclerView recyclerView;
    private List<Request> mListRequest;
    private RequestAdapter requestAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        getViews();
        setEventListener();
    }

    private void getViews() {
        imbBackPage = findViewById(R.id.imb_back_page);
        tvTitlePage = findViewById(R.id.tv_title_page);
        recyclerView = findViewById(R.id.rcv_list_request);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RequestActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(RequestActivity.this, DividerItemDecoration.VERTICAL));

        mListRequest = new ArrayList<>();
        requestAdapter = new RequestAdapter(mListRequest);
        recyclerView.setAdapter(requestAdapter);
    }

    private void setEventListener() {
        imbBackPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvTitlePage.setText("Quản lý yêu cầu");

        Query query = Request.getFirebaseReference().orderByChild("createdAt");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Request request = snapshot.getValue(Request.class);
                if(request == null) {
                    return;
                }

                if(!request.isDeny() && !request.isStatus()) {
                    Post.getPostById(request.getPostId(), new Post.IPostDataReceivedListener() {
                        @Override
                        public void onPostDataReceived(Post post) {
                            if(post == null)
                                return;

                            if(post.getUserId().equals(User.getUserInfor(RequestActivity.this).getUid())) {
                                mListRequest.add(0, request);
                                requestAdapter.notifyDataSetChanged();
                            }
                        }
                    });
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
}
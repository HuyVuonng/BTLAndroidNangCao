package com.example.shareapp.controllers.activities;

import static com.example.shareapp.controllers.constant.PostTypeConstant.TYPE_FOOD;
import static com.example.shareapp.controllers.constant.PostTypeConstant.TYPE_NON_FOOD;
import static com.example.shareapp.models.User.getUserInfor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shareapp.R;
import com.example.shareapp.controllers.adapters.FeedPostAdapter;
import com.example.shareapp.controllers.fragments.FoodFragment;
import com.example.shareapp.controllers.fragments.UserInforFragment;
import com.example.shareapp.controllers.methods.NavigationMethod;
import com.example.shareapp.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManagerPost extends AppCompatActivity {
    Button AllBtn, FoodBtn, NonFoodBtn;
    TextView emtyRcv;
    ProgressBar prgb;
    RecyclerView recyclerView;
    private List<Post> listPost;
    private FeedPostAdapter postAdapter;
    private ImageButton imbBackPage;
    String id;

    private void getView() {
        AllBtn = findViewById(R.id.btn_manage_all);
        FoodBtn = findViewById(R.id.btn_manage_food);
        NonFoodBtn = findViewById(R.id.btn_manage_non_food);
        prgb = findViewById(R.id.prgb_managePost);
        recyclerView = findViewById(R.id.rcv_list_managePost);
        imbBackPage = findViewById(R.id.imb_back_page);
        emtyRcv = findViewById(R.id.emtyrcv);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        listPost = new ArrayList<>();
        postAdapter = new FeedPostAdapter(ManagerPost.this, listPost);
        recyclerView.setAdapter(postAdapter);
    }

    private void setEventListener() {
        imbBackPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        AllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllBtn.setBackgroundTintList(getResources().getColorStateList(R.color.purple_main));
                FoodBtn.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
                NonFoodBtn.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
                renderListViewAllPost(id);
            }
        });

        FoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllBtn.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
                FoodBtn.setBackgroundTintList(getResources().getColorStateList(R.color.purple_main));
                NonFoodBtn.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
                renderListViewAllFood(id);
            }
        });

        NonFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllBtn.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
                FoodBtn.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
                NonFoodBtn.setBackgroundTintList(getResources().getColorStateList(R.color.purple_main));
                renderListViewAllNonFood(id);
            }
        });
    }

    protected void renderListViewAllPost(String uid) {
        this.prgb.setVisibility(View.VISIBLE);
        Post.getFirebaseReference().addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPost.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null && post.getUserId().equals(uid)) {
                        listPost.add(post);
                    }
                }

                postAdapter.notifyDataSetChanged();
                prgb.setVisibility(View.GONE);
                if (!listPost.isEmpty()) {
                    recyclerView.smoothScrollToPosition(0);
                    emtyRcv.setVisibility(View.INVISIBLE);
                } else {
                    emtyRcv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchFragment", error.getMessage());
            }
        });
    }

    protected void renderListViewAllFood(String uid) {
        this.prgb.setVisibility(View.VISIBLE);
        Post.getFirebaseReference().addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPost.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null && post.getUserId().equals(uid) && post.getType().equals(TYPE_FOOD)) {
                        listPost.add(post);
                    }
                }

                postAdapter.notifyDataSetChanged();
                prgb.setVisibility(View.GONE);
                if (!listPost.isEmpty()) {
                    recyclerView.smoothScrollToPosition(0);
                    emtyRcv.setVisibility(View.INVISIBLE);
                } else {
                    emtyRcv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchFragment", error.getMessage());
            }
        });
    }

    protected void renderListViewAllNonFood(String uid) {
        this.prgb.setVisibility(View.VISIBLE);
        Post.getFirebaseReference().addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPost.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null && post.getUserId().equals(uid) && post.getType().equals(TYPE_NON_FOOD)) {
                        listPost.add(post);
                    }
                }

                postAdapter.notifyDataSetChanged();
                prgb.setVisibility(View.GONE);
                if (!listPost.isEmpty()) {
                    recyclerView.smoothScrollToPosition(0);
                    emtyRcv.setVisibility(View.INVISIBLE);
                } else {
                    emtyRcv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchFragment", error.getMessage());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_post);
        getView();
        initRecyclerView();
        String uid = getIntent().getStringExtra("uid");
        if (uid != null) {
            renderListViewAllPost(uid);
            id = uid;
        } else {
            renderListViewAllPost(getUserInfor(ManagerPost.this).getUid().toString());
            id = getUserInfor(ManagerPost.this).getUid().toString();
        }
        setEventListener();
    }
}
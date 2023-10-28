package com.example.shareapp.controllers.fragments;

import static com.example.shareapp.controllers.activities.MainActivity.TYPE_FOOD;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.shareapp.R;
import com.example.shareapp.controllers.adapters.FeedPostAdapter;
import com.example.shareapp.models.Post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {
    private View mView;
    private RecyclerView rcvListFood;
    private ProgressBar prgbFood;
    private List<Post> mListPost;
    private FeedPostAdapter mPostAdapter;
    private FirebaseDatabase database;
    public FoodFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);

        getViews();
        getDataFromFirebase();
        setEventListener();
        return mView;
    }

    private void getViews() {
        database = FirebaseDatabase.getInstance();
        rcvListFood = mView.findViewById(R.id.rcv_list_food);
        prgbFood = mView.findViewById(R.id.prgb_food);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvListFood.setLayoutManager(linearLayoutManager);

        mListPost = new ArrayList<>();
        mPostAdapter = new FeedPostAdapter(getActivity(), mListPost);
        rcvListFood.setAdapter(mPostAdapter);
    }

    private void getDataFromFirebase() {
        DatabaseReference myRef = database.getReference("Posts");

        prgbFood.setVisibility(View.VISIBLE);
        Query myQuery = myRef.orderByChild("updatedAt");
        myQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                prgbFood.setVisibility(View.INVISIBLE);
                Post post = snapshot.getValue(Post.class);
                if(post != null && post.getType().equals(TYPE_FOOD)) {
                    mListPost.add(post);
                    mPostAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Post post = snapshot.getValue(Post.class);
                if(mListPost == null || mListPost.isEmpty() || post == null) {
                    return;
                }
                for (Post p : mListPost) {
                    if(p.getPostId().equals(post.getPostId())) {
                        post.setUserId(post.getUserId());
                        post.setTitle(post.getTitle());
                        post.setCount(post.getCount());
                        post.setUpdatedAt(post.getUpdatedAt());
                        post.setDelete(post.isDelete());
                        post.setLocation(post.getLocation());
                        break;
                    }
                }

                mPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if(mListPost == null || mListPost.isEmpty() || post == null) {
                    return;
                }
                for (Post p : mListPost) {
                    if(p.getPostId().equals(post.getPostId())) {
                        mListPost.remove(p);
                        break;
                    }
                }

                mPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setEventListener() {

    }
}
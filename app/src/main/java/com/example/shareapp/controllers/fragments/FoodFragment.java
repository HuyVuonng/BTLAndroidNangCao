package com.example.shareapp.controllers.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shareapp.R;
import com.example.shareapp.controllers.activities.MainActivity;
import com.example.shareapp.controllers.adapters.FeedPostAdapter;
import com.example.shareapp.controllers.adapters.PostAdapter;
import com.example.shareapp.models.Post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {
    private View mView;
    private RecyclerView rcvListFood;
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvListFood.setLayoutManager(linearLayoutManager);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
//        rcvListFood.addItemDecoration(dividerItemDecoration);

        mListPost = new ArrayList<>();
        mPostAdapter = new FeedPostAdapter(mListPost);
        rcvListFood.setAdapter(mPostAdapter);
    }

    private void getDataFromFirebase() {
        DatabaseReference myRef = database.getReference("Posts");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("FoodFragment", snapshot.toString());
//                Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
                Post post = snapshot.getValue(Post.class);
                if(post != null) {
                    mListPost.add(post);
                    mPostAdapter.notifyDataSetChanged();
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

    private void setEventListener() {

    }
}
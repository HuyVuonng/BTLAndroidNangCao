package com.example.shareapp.controllers.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shareapp.R;
import com.example.shareapp.controllers.adapters.FeedPostAdapter;
import com.example.shareapp.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private RecyclerView rv_post;
    private EditText et_search;
    private List<Post> listPost;
    private FeedPostAdapter postAdapter;
    private Button btn_search, btn_sort, btn_filter_location, btn_filter_type, btn_clear;
    private ProgressBar pb_post;
    public SearchFragment() {
        // Required empty public constructor
    }

    protected void getViews(View view) {
        this.rv_post = view.findViewById(R.id.search_rv_post);
        this.et_search = view.findViewById(R.id.search_et_search);
        this.btn_search = view.findViewById(R.id.search_btn_search);
        this.btn_sort = view.findViewById(R.id.search_btn_sort);
        this.btn_filter_location = view.findViewById(R.id.search_btn_filter_location);
        this.btn_filter_type = view.findViewById(R.id.search_btn_filter_type);
        this.pb_post = view.findViewById(R.id.search_pb_post);
        this.btn_clear = view.findViewById(R.id.search_btn_clear);

    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_post.setLayoutManager(linearLayoutManager);
        listPost = new ArrayList<>();
        postAdapter = new FeedPostAdapter(getActivity(), listPost);
        rv_post.setAdapter(postAdapter);

    }

    @SuppressLint("NotifyDataSetChanged")
    protected void setEventListener() {
        this.btn_search.setOnClickListener(v -> {
            String search = et_search.getText().toString();
            if (search.isEmpty()) {
                Toast.makeText(getContext(), "Please enter something to search", Toast.LENGTH_SHORT).show();
                return;
            }
            renderListView(et_search.getText().toString());
        });
        this.btn_clear.setOnClickListener(v -> {
            et_search.setText("");
            this.listPost.clear();
            this.postAdapter.notifyDataSetChanged();
            this.btn_clear.setVisibility(View.GONE);
        });
        this.btn_sort.setOnClickListener(v -> {
            SearchFilterBottomSheetDialog bottomSheetDialog = new SearchFilterBottomSheetDialog(
                    R.id.search_filter_btn_type_all,
                    R.id.search_filter_btn_location_all,
                    R.id.search_filter_sort_rb_latest);
            bottomSheetDialog.show(getChildFragmentManager(), "filter");
        });
    }

    protected void renderListView(String search) {
        this.pb_post.setVisibility(View.VISIBLE);
        Post.getFirebaseReference().addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPost.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null && post.getTitle().toLowerCase().contains(search.toLowerCase())) {
                        listPost.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
                pb_post.setVisibility(View.GONE);
                if (!listPost.isEmpty()) {
                    rv_post.smoothScrollToPosition(0);
                    btn_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchFragment", error.getMessage());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        this.getViews(view);
        this.initRecyclerView();
        this.setEventListener();
        // Inflate the layout for this fragment
        return view;
    }
}
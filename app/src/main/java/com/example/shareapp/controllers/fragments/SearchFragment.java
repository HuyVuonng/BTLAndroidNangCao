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
import com.example.shareapp.controllers.constant.PostTypeConstant;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {


    private int filter_type = R.id.search_filter_btn_type_all, filter_location = R.id.search_filter_btn_location_all, filter_sort = R.id.search_filter_sort_rb_latest;
    private List<Integer> typeList = new ArrayList<>(Arrays.asList(R.id.search_filter_btn_product, R.id.search_filter_btn_user));
    private List<Integer> userFilterKeyList = new ArrayList<>(Arrays.asList(R.id.search_filter_btn_user_email, R.id.search_filter_btn_user_name, R.id.search_filter_btn_user_description));
    private RecyclerView rv_post;
    private EditText et_search;
    private List<Post> listPost;
    private List<User> listUser;
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
        listUser = new ArrayList<>();

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
            this.listUser.clear();
            this.filter_type = R.id.search_filter_btn_type_all;
            this.displayFilterType();
            this.filter_location = R.id.search_filter_btn_location_all;
            this.displayFilterLocation();
            this.filter_sort = R.id.search_filter_sort_rb_latest;
            this.postAdapter.notifyDataSetChanged();
            this.typeList = new ArrayList<>(Arrays.asList(R.id.search_filter_btn_product, R.id.search_filter_btn_user));
            this.userFilterKeyList = new ArrayList<>(Arrays.asList(R.id.search_filter_btn_user_email, R.id.search_filter_btn_user_name, R.id.search_filter_btn_user_description));
            this.btn_clear.setVisibility(View.GONE);
        });
        this.btn_sort.setOnClickListener(v -> {
            SearchFilterBottomSheetDialog bottomSheetDialog = new SearchFilterBottomSheetDialog(
                    this.filter_type,
                    this.filter_location,
                    this.filter_sort,
                    this.typeList,
                    this.userFilterKeyList,
                    data -> {
                        this.filter_type = data.getType();
                        this.filter_location = data.getLocation();
                        this.filter_sort = data.getSort();
                        this.typeList = data.getTypeList();
                        this.userFilterKeyList = data.getUserFilterKeyList();
                        if (!this.et_search.getText().toString().isEmpty()) {
                            this.renderListView(this.et_search.getText().toString());
                        }
                    });
            bottomSheetDialog.show(getChildFragmentManager(), "search_filter_bottom_sheet");
        });
        this.btn_filter_type.setOnClickListener(v -> {
            this.filter_type = R.id.search_filter_btn_type_all;
            this.displayFilterType();
            this.renderListView(et_search.getText().toString());
        });
        this.btn_filter_location.setOnClickListener(v -> {
            this.filter_location = R.id.search_filter_btn_location_all;
            this.displayFilterLocation();
            this.renderListView(et_search.getText().toString());
        });
    }

    protected void renderListView(String search) {
        this.pb_post.setVisibility(View.VISIBLE);
        this.listPost.clear();
        this.listUser.clear();
        if(this.typeList.contains(R.id.search_filter_btn_product)) {
            Post.getFirebaseReference().addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Post post = dataSnapshot.getValue(Post.class);
                        if (post != null && post.getTitle().toLowerCase().contains(search.toLowerCase())) {
                            if (!checkPostType(post)) {
                                continue;
                            }
                            if (!checkPostLocation(post)) {
                                continue;
                            }
                            listPost.add(post);
                        }
                    }
                    if (filter_sort == R.id.search_filter_sort_rb_latest) {
                        listPost.sort((o1, o2) -> (int) (o2.getUpdatedAt() - o1.getUpdatedAt()));
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
                    Log.e("SearchFragment - Post", error.getMessage());
                }
            });
        }

        if (this.typeList.contains(R.id.search_filter_btn_user)) {
            User.getFirebaseReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null && userFilterKeyList.contains(R.id.search_filter_btn_user_name) && user.getFullName().toLowerCase().contains(search.toLowerCase())) {
                            listUser.add(user);
                            continue;
                        }
                        if (user != null && userFilterKeyList.contains(R.id.search_filter_btn_user_email) && user.getEmail().toLowerCase().contains(search.toLowerCase())) {
                            listUser.add(user);
                            continue;
                        }
                        if (user != null && userFilterKeyList.contains(R.id.search_filter_btn_user_description)) {
//                            listUser.add(user);
//                            @todo: add search by description
                        }

                    }
                    pb_post.setVisibility(View.GONE);
                    if (!listUser.isEmpty()) {
                        rv_post.smoothScrollToPosition(0);
                        btn_clear.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("SearchFragment - User", error.getMessage());
                }
            });
        }
    }

    protected boolean checkPostType(Post post) {
        this.displayFilterType();
        if (this.filter_type == R.id.search_filter_btn_type_all) {
            return true;
        }
        else if (this.filter_type == R.id.search_filter_btn_type_food && Objects.equals(post.getType(), PostTypeConstant.TYPE_FOOD)) {
            return true;
        }
        else if (this.filter_type == R.id.search_filter_btn_type_non_food && Objects.equals(post.getType(), PostTypeConstant.TYPE_NON_FOOD)) {
            return true;
        }
        return false;
    }

    protected void displayFilterType() {
        if (this.filter_type == R.id.search_filter_btn_type_all) {
            this.btn_filter_type.setText(R.string.search_filter_type);
            this.btn_filter_type.setVisibility(View.GONE);
        }
        else if (this.filter_type == R.id.search_filter_btn_type_food) {
            this.btn_filter_type.setText("Loại sản phẩm: " + PostTypeConstant.TYPE_FOOD);
            this.btn_filter_type.setVisibility(View.VISIBLE);
        }
        else if (this.filter_type == R.id.search_filter_btn_type_non_food) {
            this.btn_filter_type.setText("Loại sản phẩm: " + PostTypeConstant.TYPE_NON_FOOD);
            this.btn_filter_type.setVisibility(View.VISIBLE);
        }
    }

    protected boolean checkPostLocation(Post post) {
        this.displayFilterLocation();
        if(this.filter_location == R.id.search_filter_btn_location_all) {
            return true;
        }
//        @todo: check post location
        return false;
    }

    protected void displayFilterLocation() {
        if (this.filter_location == R.id.search_filter_btn_location_all) {
            this.btn_filter_location.setText(R.string.search_filter_location);
            this.btn_filter_location.setVisibility(View.GONE);
        }
        else if (this.filter_location == R.id.search_filter_btn_location_1) {
            this.btn_filter_location.setText("Khoảng cách: 1km");
            this.btn_filter_location.setVisibility(View.VISIBLE);
        }
        else if (this.filter_location == R.id.search_filter_btn_location_3) {
            this.btn_filter_location.setText("Khoảng cách: 3km");
            this.btn_filter_location.setVisibility(View.VISIBLE);
        }
        else if (this.filter_location == R.id.search_filter_btn_location_5) {
            this.btn_filter_location.setText("Khoảng cách: 5km");
            this.btn_filter_location.setVisibility(View.VISIBLE);
        }
        else if (this.filter_location == R.id.search_filter_btn_location_10) {
            this.btn_filter_location.setText("Khoảng cách: 10km");
            this.btn_filter_location.setVisibility(View.VISIBLE);
        }
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
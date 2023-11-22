package com.example.shareapp.controllers.fragments;

import static com.example.shareapp.controllers.activities.MapsActivity.REQUEST_LOCATION_PERMISSION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shareapp.R;
import com.example.shareapp.controllers.adapters.FeedPostAdapter;
import com.example.shareapp.controllers.adapters.UserAdapter;
import com.example.shareapp.controllers.constant.PostTypeConstant;
import com.example.shareapp.models.Location;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
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
    private RecyclerView rv_post, rv_user;
    private EditText et_search;
    private List<Post> listPost;
    private List<User> listUser;
    private FeedPostAdapter postAdapter;
    private UserAdapter userAdapter;
    private Button btn_search, btn_sort, btn_filter_location, btn_filter_type, btn_clear;
    private ProgressBar pb_post;
    private TabLayout tl_result;
    private  Location user_location;
    private FusedLocationProviderClient mFusedLocationClient;
    private Context mContext;

    public SearchFragment() {
        // Required empty public constructor

    }

    protected void getViews(View view) {
        this.rv_post = view.findViewById(R.id.search_rv_post);
        this.rv_user = view.findViewById(R.id.search_rv_user);
        this.et_search = view.findViewById(R.id.search_et_search);
        this.btn_search = view.findViewById(R.id.search_btn_search);
        this.btn_sort = view.findViewById(R.id.search_btn_sort);
        this.btn_filter_location = view.findViewById(R.id.search_btn_filter_location);
        this.btn_filter_type = view.findViewById(R.id.search_btn_filter_type);
        this.pb_post = view.findViewById(R.id.search_pb_post);
        this.btn_clear = view.findViewById(R.id.search_btn_clear);
        this.tl_result = view.findViewById(R.id.search_tl_result);
    }

    private void initRecyclerView() {
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_post.setLayoutManager(new LinearLayoutManager(getActivity()));
        listPost = new ArrayList<>();
        postAdapter = new FeedPostAdapter(getActivity(), listPost);
        rv_post.setAdapter(postAdapter);
        rv_user.setLayoutManager(new LinearLayoutManager(getActivity()));
        listUser = new ArrayList<>();
        userAdapter = new UserAdapter(getActivity(), listUser);
        rv_user.setAdapter(userAdapter);
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
            this.userAdapter.notifyDataSetChanged();
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
        this.tl_result.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        setActiveTab();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        setActiveTab();
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        setActiveTab();
                    }
                }
        );
    }

    protected void renderListView(String search) {
        this.pb_post.setVisibility(View.VISIBLE);
        this.listPost.clear();
        this.listUser.clear();
        if (this.typeList.contains(R.id.search_filter_btn_product) && !this.typeList.contains(R.id.search_filter_btn_user)) {
            this.tl_result.getTabAt(0).select();
        } else if (!this.typeList.contains(R.id.search_filter_btn_product) && this.typeList.contains(R.id.search_filter_btn_user)) {
            this.tl_result.getTabAt(1).select();
        } else {
            this.tl_result.getTabAt(0).select();
        }
        if (this.typeList.contains(R.id.search_filter_btn_product)) {
            Post.getFirebaseReference().addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Post post = dataSnapshot.getValue(Post.class);
                        if (post != null && post.getTitle().toLowerCase().contains(search.toLowerCase()) && !post.isDelete()) {
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
                        if (user != null && userFilterKeyList.contains(R.id.search_filter_btn_user_name) && user.getFullName().toLowerCase().contains(search.toLowerCase()) && !user.getBlock()) {
                            listUser.add(user);
                            continue;
                        }
                        if (user != null && userFilterKeyList.contains(R.id.search_filter_btn_user_email) && user.getEmail().toLowerCase().contains(search.toLowerCase()) && !user.getBlock()) {
                            listUser.add(user);
                            continue;
                        }
                        if (user != null && userFilterKeyList.contains(R.id.search_filter_btn_user_description) && !user.getBlock()) {
//                            listUser.add(user);
//                            @todo: add search by description
                        }

                    }
                    userAdapter.notifyDataSetChanged();
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
        } else if (this.filter_type == R.id.search_filter_btn_type_food && Objects.equals(post.getType(), PostTypeConstant.TYPE_FOOD)) {
            return true;
        } else if (this.filter_type == R.id.search_filter_btn_type_non_food && Objects.equals(post.getType(), PostTypeConstant.TYPE_NON_FOOD)) {
            return true;
        }
        return false;
    }

    protected void displayFilterType() {
        if (this.filter_type == R.id.search_filter_btn_type_all) {
            this.btn_filter_type.setText(R.string.search_filter_type);
            this.btn_filter_type.setVisibility(View.GONE);
        } else if (this.filter_type == R.id.search_filter_btn_type_food) {
            this.btn_filter_type.setText("Loại sản phẩm: " + PostTypeConstant.TYPE_FOOD);
            this.btn_filter_type.setVisibility(View.VISIBLE);
        } else if (this.filter_type == R.id.search_filter_btn_type_non_food) {
            this.btn_filter_type.setText("Loại sản phẩm: " + PostTypeConstant.TYPE_NON_FOOD);
            this.btn_filter_type.setVisibility(View.VISIBLE);
        }
    }

    protected boolean checkPostLocation(Post post) {
        this.displayFilterLocation();
        if (this.filter_location != R.id.search_filter_btn_location_all) {
            this.getLastLocation();
        }
        if (this.filter_location == R.id.search_filter_btn_location_all) {
            return true;
        } else if (this.user_location == null) {
            this.filter_location = R.id.search_filter_btn_location_all;
            Toast.makeText(mContext, "Please turn on location or set your default location!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (this.filter_location == R.id.search_filter_btn_location_1) {
            return this.checkHaversineDistance(post.getLocation(), user_location, 1.0);
        } else if (this.filter_location == R.id.search_filter_btn_location_3) {
            return this.checkHaversineDistance(post.getLocation(), user_location, 3.0);
        } else if (this.filter_location == R.id.search_filter_btn_location_5) {
            return this.checkHaversineDistance(post.getLocation(), user_location, 5.0);
        } else if (this.filter_location == R.id.search_filter_btn_location_10) {
            return this.checkHaversineDistance(post.getLocation(), user_location, 10.0);
        }
        return false;
    }

    protected void displayFilterLocation() {
        if (this.filter_location == R.id.search_filter_btn_location_all) {
            this.btn_filter_location.setText(R.string.search_filter_location);
            this.btn_filter_location.setVisibility(View.GONE);
        } else if (this.filter_location == R.id.search_filter_btn_location_1) {
            this.btn_filter_location.setText("Khoảng cách: 1km");
            this.btn_filter_location.setVisibility(View.VISIBLE);
        } else if (this.filter_location == R.id.search_filter_btn_location_3) {
            this.btn_filter_location.setText("Khoảng cách: 3km");
            this.btn_filter_location.setVisibility(View.VISIBLE);
        } else if (this.filter_location == R.id.search_filter_btn_location_5) {
            this.btn_filter_location.setText("Khoảng cách: 5km");
            this.btn_filter_location.setVisibility(View.VISIBLE);
        } else if (this.filter_location == R.id.search_filter_btn_location_10) {
            this.btn_filter_location.setText("Khoảng cách: 10km");
            this.btn_filter_location.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkHaversineDistance(Location a, Location b, Double distance) {
        double lat1 = a.getLatitude();
        double lon1 = a.getLongitude();
        double lat2 = b.getLatitude();
        double lon2 = b.getLongitude();
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double c = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371.0;
        double distance1 = 2 * Math.asin(Math.sqrt(c)) * rad;
        return distance1 <= distance;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                android.location.Location location = task.getResult();
                if (location == null) {
                    requestNewLocationData();
                } else {
                    this.user_location = new Location(location.getLongitude(), location.getLatitude());
                }
            });
        }
        else {
            this.requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(
                this.mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.mContext);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                android.location.Location mLastLocation = locationResult.getLastLocation();
                if (mLastLocation != null) {
                    user_location = new Location(mLastLocation.getLongitude(), mLastLocation.getLatitude());
                    return;
                }
                Toast.makeText(getContext(), "Please turn on location", Toast.LENGTH_SHORT).show();
            }
        }, Looper.myLooper());
    }

    private void requestPermissions() {
        requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_LOCATION_PERMISSION);
    }

    private void setActiveTab() {
        if (this.tl_result.getSelectedTabPosition() == 0) {
            this.rv_post.setVisibility(View.VISIBLE);
            this.rv_user.setVisibility(View.GONE);
        } else {
            this.rv_post.setVisibility(View.GONE);
            this.rv_user.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (User.getUserInfor(this.mContext).location != null) {
            this.user_location = new Location(
                    User.getUserInfor(this.mContext).location.getLongitude(),
                    User.getUserInfor(this.mContext).location.getLatitude()
            );
        }
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
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
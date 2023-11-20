package com.example.shareapp.controllers.activities;

import static com.example.shareapp.controllers.activities.MainActivity.ACTION_NAME;
import static com.example.shareapp.controllers.activities.MainActivity.ACTION_UPDATE_POST;
import static com.example.shareapp.controllers.activities.MainActivity.MY_POST;
import static com.example.shareapp.controllers.activities.MainActivity.NAME_TYPE;
import static com.example.shareapp.models.Post.hidePost;
import static com.example.shareapp.models.Post.showPost;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shareapp.R;
import com.example.shareapp.controllers.methods.DateTimeMethod;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyPostDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Toolbar toolbar;
    private ImageButton imbBackPage;
    private ImageView imvImagePost, cimvImagePoster;
    private TextView tvTitlePage, tvFullNamePoster, tvTitlePost, tvCreatedAt, tvQuantity, tvDescription;
    private Button btnUpdate, btnDelete, btnShowPost;
    private Post mPost;
    private GoogleMap mMap;
    private SupportMapFragment f_map;
    FusedLocationProviderClient mFusedLocationClient;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_detail);

        getViews();
        getDataIntent();
        initService();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        LatLng current_locale = new LatLng(this.mPost.getLocation().getLatitude(), this.mPost.getLocation().getLongitude());
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.addMarker(new MarkerOptions().position(current_locale).title("At here!"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current_locale, 16.0f));
        this.setEventListener();
    }

    protected void initService() {
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.f_map.getMapAsync(this);
    }

    private void getViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imbBackPage = findViewById(R.id.imb_back_page);
        tvTitlePage = findViewById(R.id.tv_title_page);
        imvImagePost = findViewById(R.id.iv_image_post);
        cimvImagePoster = findViewById(R.id.cimg_image_poster);
        tvFullNamePoster = findViewById(R.id.tv_full_name_poster);
        tvTitlePost = findViewById(R.id.tv_title_post);
        tvCreatedAt = findViewById(R.id.tv_created_at);
        tvQuantity = findViewById(R.id.tv_quantity_product);
        tvDescription = findViewById(R.id.tv_description);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
        btnShowPost = findViewById(R.id.btn_showPost);
        f_map = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.my_post_detail_f_map);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void getDataIntent() {
        Intent intent = getIntent();
        mPost = (Post) intent.getSerializableExtra("item_post");

        if (mPost != null && !TextUtils.isEmpty(mPost.getImage()))
            Glide.with(MyPostDetailActivity.this).load(mPost.getImage()).into(imvImagePost);
        tvCreatedAt.setText("Từ " + DateTimeMethod.timeDifference(mPost.getCreatedAt()));
        tvTitlePost.setText(mPost.getTitle());
        tvQuantity.setText(String.valueOf(mPost.getCount()));
        tvDescription.setText(mPost.getDescription());
        if (mPost.isDelete()) {
            btnShowPost.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
        }
        new User().getUserById(mPost.getUserId(), new User.IUserDataReceivedListener() {
            @Override
            public Boolean onUserDataReceived(User user) {
                if (user != null) {
                    if (user.getAvata() != null) {
                        Glide.with(MyPostDetailActivity.this).load(user.getAvata()).into(cimvImagePoster);
                    }
                    tvFullNamePoster.setText(user.getFullName() + " đang cho đi");
                }
                return null;
            }
        });
    }

    private void setEventListener() {
        tvTitlePage.setText(mPost.getTitle());
        imbBackPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPostDetailActivity.this, CreatePostActivity.class);
                intent.putExtra(ACTION_NAME, ACTION_UPDATE_POST);
                intent.putExtra(MY_POST, mPost);
                intent.putExtra(NAME_TYPE, mPost.getType());
                startActivity(intent);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MyPostDetailActivity.this)
                        .setTitle("Thông báo")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("Bạn có chắc muốn ẩn bài viết này không?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hidePost(mPost.getPostId());
                                Toast.makeText(MyPostDetailActivity.this, "Đã ẩn bài viết.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MyPostDetailActivity.this, MainActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        btnShowPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MyPostDetailActivity.this)
                        .setTitle("Thông báo")
                        .setMessage("Bạn có chắc muốn hiện lại bài viết này không?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showPost(mPost.getPostId());
                                Toast.makeText(MyPostDetailActivity.this, "Đã hiện bài viết.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MyPostDetailActivity.this, MainActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
    }
}
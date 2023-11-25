package com.example.shareapp.controllers.activities;

import static com.example.shareapp.controllers.constant.ReportTypeConstant.TYPE_POST;
import static com.example.shareapp.models.User.getUserInfor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shareapp.R;
import com.example.shareapp.controllers.constant.NotifiTypeConstant;
import com.example.shareapp.controllers.methods.DateTimeMethod;
import com.example.shareapp.models.Notification;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.Report;
import com.example.shareapp.models.Request;
import com.example.shareapp.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class PostDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Toolbar toolbar;
    private ImageButton imbBackPage, imbReport;
    private ImageView imvImagePost, cimvImagePoster;
    private TextView tvTitlePage, tvFullNamePoster, tvTitlePost, tvCreatedAt, tvQuantity, tvDescription;
    private Button btnRequestPost;
    private Post mPost;
    private Boolean isReported = false;

    private GoogleMap mMap;
    private SupportMapFragment f_map;
    FusedLocationProviderClient mFusedLocationClient;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        getViews();
        getDataIntent();
        checkReportPost();
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
        btnRequestPost = findViewById(R.id.btn_request_post);
        imbReport = findViewById(R.id.imb_report);
        f_map = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.post_detail_f_map);
        progressDialog = new ProgressDialog(PostDetailActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void getDataIntent() {
        Intent intent = getIntent();
        mPost = (Post) intent.getSerializableExtra("item_post");

        if (!TextUtils.isEmpty(mPost.getImage()))
            Glide.with(PostDetailActivity.this).load(mPost.getImage()).into(imvImagePost);
        tvCreatedAt.setText("Từ " + DateTimeMethod.timeDifference(mPost.getCreatedAt()));
        tvTitlePost.setText(mPost.getTitle());
        tvQuantity.setText(String.valueOf(mPost.getCount()));
        tvDescription.setText(mPost.getDescription());
        new User().getUserById(mPost.getUserId(), new User.IUserDataReceivedListener() {
            @Override
            public Boolean onUserDataReceived(User user) {
                if (user != null) {
                    if (user.getAvata() != null) {
                        Glide.with(PostDetailActivity.this).load(user.getAvata()).into(cimvImagePoster);
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

        btnRequestPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check time: 10p mới được request 1 lần
                Request.getRequestLast(User.getUserInfor(PostDetailActivity.this).getUid(),
                        mPost.getPostId(),
                        new Request.IRequestReceivedListener() {
                            @Override
                            public void onRequestDataReceived(Request request) {
                                boolean isCreate = false;
                                int minuteRequest = 2;
                                if(request == null) {
                                    isCreate = true;
                                } else {
                                    if(DateTimeMethod.minutesDifference(request.getCreatedAt()) >= minuteRequest) {
                                        isCreate = true;
                                    } else {
                                        isCreate = false;
                                    }
                                }

                                if(isCreate) {
                                    createRequestNotifi();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                                    builder.setTitle("Thông báo");
                                    builder.setMessage("Bạn không thể gửi yêu cầu trong " + (minuteRequest - DateTimeMethod.minutesDifference(request.getUpdatedAt())) + " phút nữa");
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            }
                        });


            }
        });

        cimvImagePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                mPost = (Post) intent.getSerializableExtra("item_post");
                Intent i = new Intent(getApplicationContext(), UserInforPublicActivity.class);
                i.putExtra("uid", mPost.getUserId());
                startActivity(i);
            }
        });

        imbReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn có chắc chắn muốn báo cáo bài viết này không?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isReported) {
                            Intent intent = getIntent();
                            mPost = (Post) intent.getSerializableExtra("item_post");
                            Report.CreateNewReport(UUID.randomUUID().toString(), getUserInfor(PostDetailActivity.this).getUid(), mPost.getUserId(), mPost.getPostId(), TYPE_POST);
                            Toast.makeText(getApplicationContext(), "Báo cáo thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Bạn đã báo cáo bài đăng này", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void checkReportPost() {
        Intent intent = getIntent();
        mPost = (Post) intent.getSerializableExtra("item_post");
        String postReportID = mPost.getPostId();
        Report.getFirebaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Report report = dataSnapshot.getValue(Report.class);
                    if (report != null && report.getReporterId().equals(getUserInfor(PostDetailActivity.this).getUid()) && report.getPostId().equals(postReportID) && report.getType().equals(TYPE_POST)) {
                        isReported = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createRequestNotifi() {
        Notification notification = new Notification();
        notification.setNotifiId(UUID.randomUUID().toString());
        notification.setTargetId(mPost.getUserId());
        notification.setType(NotifiTypeConstant.TYPE_REQUEST);
        notification.setTitle(getUserInfor(PostDetailActivity.this).getFullName() + " đang yêu cầu nhận '" + mPost.getTitle() + "'.");
        notification.setContent("Bạn có đồng ý cho đi không?");
        notification.setStatus(false);
        notification.setCreatedAt(System.currentTimeMillis());

        // add vao db
        Notification.createNotification(notification);

        Request request = new Request();
        request.setRequestId(UUID.randomUUID().toString());
        request.setNotificationId(notification.getNotifiId());
        request.setPostId(mPost.getPostId());
        request.setUserId(getUserInfor(PostDetailActivity.this).getUid());
        request.setStatus(false);
        request.setCreatedAt(System.currentTimeMillis());
        request.setUpdatedAt(System.currentTimeMillis());

        // add vao db
        Request.createRequest(request);

        Toast.makeText(PostDetailActivity.this, "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(PostDetailActivity.this, MainActivity.class));
    }
}
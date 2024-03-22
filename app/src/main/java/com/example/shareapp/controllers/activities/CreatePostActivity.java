package com.example.shareapp.controllers.activities;

import static com.example.shareapp.controllers.activities.MainActivity.ACTION_CREATE_POST;
import static com.example.shareapp.controllers.activities.MainActivity.ACTION_NAME;
import static com.example.shareapp.controllers.activities.MainActivity.MY_POST;
import static com.example.shareapp.controllers.activities.MainActivity.NAME_TYPE;
import static com.example.shareapp.controllers.activities.MainActivity.TYPE_FOOD;
import static com.example.shareapp.controllers.activities.MapsActivity.REQUEST_LOCATION_PERMISSION;
import static com.example.shareapp.controllers.constant.LocationConstant.LATITUDE;
import static com.example.shareapp.controllers.constant.LocationConstant.LONGITUDE;
import static com.example.shareapp.models.User.getUserInfor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.shareapp.R;
import com.example.shareapp.controllers.constant.LocationConstant;
import com.example.shareapp.models.Location;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Toolbar toolbar;
    private ImageButton imbBackPage;
    private TextView tvTitlePage;
    private ImageButton imbImagePost;
    private TextView tvErrorImage;
    private EditText edtTitle, edtDescription, edtQuantity, edtPrice;
    private Button btnSubmit;
    private String typePost;
    private ProgressDialog progressDialog;
    private Uri uriImage;
    private Post mPost;
    private SupportMapFragment mapFragment;
    private String mAction;

    private GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationClient;
    private double latitude, longitude;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == LocationConstant.REQUEST_GET_MAP_LOCATION) {
                    Intent data = result.getData();
                    if (data != null) {
                        Double longitude = data.getDoubleExtra(LONGITUDE, 0);
                        Double latitude = data.getDoubleExtra(LATITUDE, 0);
                        this.setCurrentLocation(longitude, latitude);

                    }
                }
            });
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        this.getViews();
        this.getDataIntent();
        this.initService();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        if (this.mPost != null) {
            this.longitude = mPost.getLocation().getLongitude();
            this.latitude = mPost.getLocation().getLatitude();
        } else if (User.getUserInfor(this).location.getLatitude() != 0 && User.getUserInfor(this).location.getLongitude() != 0){
            this.longitude = User.getUserInfor(this).location.getLongitude();
            this.latitude = User.getUserInfor(this).location.getLatitude();

        } else  {
            this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            this.getLastLocation();
        }
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        if(this.latitude != 0 && this.longitude != 0) {
           this.setCurrentLocation(this.longitude, this.latitude);
        }
        this.setEventListener();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                android.location.Location location = task.getResult();
                if (location == null) {
                    requestNewLocationData();
                } else {
                    this.setCurrentLocation( location.getLongitude(), location.getLatitude());
                }
            });
        }
        else {
            this.requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                android.location.Location mLastLocation = locationResult.getLastLocation();
                if(latitude == 0 && longitude == 0) {
                    latitude =  mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                }
            }
        }, Looper.myLooper());
    }

    private void requestPermissions() {
        requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_LOCATION_PERMISSION);
    }

    protected void setCurrentLocation(Double longitude, Double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        if(this.mMap != null) {
            mMap.clear();
            LatLng current_locale = new LatLng(this.latitude, this.longitude);
            mMap.addMarker(new MarkerOptions().position(current_locale).title("You set location here!"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current_locale, 16.0f));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions() && this.longitude == 0 && this.latitude == 0) {
            getLastLocation();
        }
    }

    protected void initService() {
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.mapFragment.getMapAsync(this);
    }

    private void getViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imbBackPage = findViewById(R.id.imb_back_page);
        tvTitlePage = findViewById(R.id.tv_title_page);
        imbImagePost = findViewById(R.id.imv_image_post);
        tvErrorImage = findViewById(R.id.tv_error_no_image);
        edtTitle = findViewById(R.id.edt_title_post);
        edtDescription = findViewById(R.id.edt_description);
        edtQuantity = findViewById(R.id.edt_quantity);
        edtPrice = findViewById(R.id.edt_price);
        btnSubmit = findViewById(R.id.btn_submit);
        progressDialog = new ProgressDialog(this);
        this.mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.create_post_f_map);
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        mAction = intent.getStringExtra(ACTION_NAME);
        typePost = intent.getStringExtra(NAME_TYPE);
        mPost = (Post) intent.getSerializableExtra(MY_POST);
    }

    private void setEventListener() {
        if(mAction.equals(ACTION_CREATE_POST)) {
            btnSubmit.setText("Đăng");
        } else {
            btnSubmit.setText("Cập nhật");
        }
        String title;
        if(typePost.equals(TYPE_FOOD)) {
            title = "Free Food";
        } else {
            title = "Free Non-Food";
        }
        tvTitlePage.setText(title);

        if(mPost != null) {
            Glide.with(CreatePostActivity.this).load(mPost.getImage()).into(imbImagePost);
            edtTitle.setText(mPost.getTitle());
            edtQuantity.setText(String.valueOf(mPost.getCount()));
            edtPrice.setText(String.valueOf(mPost.getPrice()));
            edtDescription.setText(mPost.getDescription());
        }

        imbBackPage.setOnClickListener(view -> finish());
        imbImagePost.setOnClickListener(view -> ImagePicker.with(CreatePostActivity.this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start());

        btnSubmit.setOnClickListener(view -> {
            if(validateData()) {
                if (mPost == null) {
                    Post post = new Post();
                    post.setPostId(UUID.randomUUID().toString());
                    post.setUserId(getUserInfor(CreatePostActivity.this).getUid());
                    post.setTitle(edtTitle.getText().toString());
                    post.setType(typePost);
                    post.setCount(Integer.parseInt(edtQuantity.getText().toString()));
                    post.setPrice(Long.parseLong(edtPrice.getText().toString()));
                    post.setCreatedAt(System.currentTimeMillis());
                    post.setUpdatedAt(System.currentTimeMillis());
                    post.setDelete(false);
                    post.setLocation(new Location(this.longitude, this.latitude));
                    post.setDescription(edtDescription.getText().toString());

                    createPost(post);
                } else {
                    mPost.setTitle(edtTitle.getText().toString());
                    mPost.setCount(Integer.parseInt(edtQuantity.getText().toString()));
                    mPost.setPrice(Long.parseLong(edtPrice.getText().toString()));
                    mPost.setUpdatedAt(System.currentTimeMillis());
                    mPost.setLocation(new Location(this.longitude, this.latitude));
                    mPost.setDescription(edtDescription.getText().toString());

                    updatePost(mPost);
                }
            }

        });

        this.mMap.setOnMapClickListener(latLng -> {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra(LATITUDE, this.latitude);
            intent.putExtra(LONGITUDE, this.longitude);
            this.activityResultLauncher.launch(intent);
        });
    }

    private boolean validateData() {
        boolean isOk = true;
        if(mPost == null && uriImage == null) {
            tvErrorImage.setText("Vui lòng chọn ảnh");
            isOk = false;
        }
        else {
            tvErrorImage.setText("");
        }

        if(TextUtils.isEmpty(edtTitle.getText().toString())) {
            edtTitle.setError("Tiêu đề không được để trống");
            isOk = false;
        }

        if(TextUtils.isEmpty(edtDescription.getText().toString())) {
            edtDescription.setError("Mô tả không được để trống");
            isOk = false;
        }

        if (this.longitude == 0 && this.latitude == 0) {
            Toast.makeText(this, "Vui lòng chọn địa điểm", Toast.LENGTH_SHORT).show();
            isOk = false;
        }

        return isOk;
    }

    private void createPost(Post post) {
        DatabaseReference myRef = Post.getFirebaseReference();
        progressDialog.show();

        // Đẩy ảnh lên firebase
        uploadImageToFirebase(post, myRef);
    }

    private void resetData() {
        edtTitle.setText("");
        edtDescription.setText("");
        edtQuantity.setText("");
        imbImagePost.setImageResource(R.drawable.no_img);
        uriImage = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            uriImage = data.getData();
            imbImagePost.setImageURI(uriImage);
        }
    }

    private void uploadImageToFirebase(Post post, DatabaseReference myRef) {
        StorageReference myStore = FirebaseStorage.getInstance().getReference().child("Images/Post").child(uriImage.getLastPathSegment());
        myStore.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri resultUri = uriTask.getResult();
                String urlImageString = resultUri.toString();

                post.setImage(urlImageString);

                String pathObject = String.valueOf(post.getPostId());
                myRef.child(pathObject).setValue(post, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        progressDialog.dismiss();
                        resetData();
                        Toast.makeText(CreatePostActivity.this, "Create post successed", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreatePostActivity.this, MainActivity.class));
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Lỗi upload ảnh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePost(Post post) {
        DatabaseReference myRef = Post.getFirebaseReference();
        progressDialog.show();

        if(uriImage == null) {
            myRef.child(post.getPostId()).setValue(post, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    progressDialog.dismiss();
                    Toast.makeText(CreatePostActivity.this, "Update post successed", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreatePostActivity.this, MainActivity.class));
                }
            });
        } else {
            StorageReference myStore = FirebaseStorage.getInstance().getReference().child("Images/Post").child(uriImage.getLastPathSegment());
            myStore.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri resultUri = uriTask.getResult();
                    String urlImageString = resultUri.toString();

                    post.setImage(urlImageString);
                    myRef.child(post.getPostId()).setValue(post, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            progressDialog.dismiss();
                            Toast.makeText(CreatePostActivity.this, "Update post successed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreatePostActivity.this, MainActivity.class));
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Lỗi upload ảnh", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}

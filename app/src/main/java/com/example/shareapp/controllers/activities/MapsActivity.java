package com.example.shareapp.controllers.activities;

import static com.example.shareapp.controllers.constant.LocationConstant.LATITUDE;
import static com.example.shareapp.controllers.constant.LocationConstant.LONGITUDE;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.shareapp.R;
import com.example.shareapp.controllers.constant.LocationConstant;
import com.example.shareapp.models.User;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.shareapp.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static int REQUEST_LOCATION_PERMISSION = 1000;
    private GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationClient;
    private double latitude, longitude;
    private ImageButton ib_back;
    private Button btn_apply;

    protected void setEventListener() {
        this.mMap.setOnMapClickListener(latLng -> {
            if (this.latitude != latLng.latitude && this.longitude != latLng.longitude) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("New Position!"));
                this.latitude = latLng.latitude;
                this.longitude = latLng.longitude;
            }
        });
        this.ib_back.setOnClickListener(v -> finish());
        this.btn_apply.setOnClickListener(v -> {
            setResult(LocationConstant.REQUEST_GET_MAP_LOCATION,
                    getIntent()
                            .putExtra(LATITUDE, this.latitude)
                            .putExtra(LONGITUDE, this.longitude));
            finish();
        });
    }

    protected void getView(View view) {
        this.ib_back = view.findViewById(R.id.maps_ib_back);
        this.btn_apply = view.findViewById(R.id.maps_btn_apply);
    }

    protected void initService() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.shareapp.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.getView(binding.getRoot());
        Intent getIntent = getIntent();
        if (getIntent.getDoubleExtra(LONGITUDE, 0) != 0
                && getIntent.getDoubleExtra(LATITUDE, 0) != 0) {
            this.latitude =getIntent.getDoubleExtra(LATITUDE, 0);
            this.longitude =getIntent.getDoubleExtra(LONGITUDE, 0);
            this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            this.initService();
        } else {
            if(User.getUserInfor(this).location.getLatitude() != 0 && User.getUserInfor(this).location.getLongitude() != 0) {
                this.latitude = User.getUserInfor(this).location.getLatitude();
                this.longitude = User.getUserInfor(this).location.getLongitude();
                this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                this.initService();
            } else {
                this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                this.getLastLocation();
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }


    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location == null) {
                    requestNewLocationData();
                } else {
                    this.latitude =  location.getLatitude();
                    this.longitude = location.getLongitude();
                    this.initService();
                }
            });
        }
        else {
            this.requestPermissions();
        }
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
                Location mLastLocation = locationResult.getLastLocation();
                latitude =  mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }
        }, Looper.myLooper());
    }

    private void requestPermissions() {
        requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_LOCATION_PERMISSION);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions() && this.longitude == 0 && this.latitude == 0) {
            getLastLocation();
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        LatLng current_locale = new LatLng(this.latitude, this.longitude);
        mMap.addMarker(new MarkerOptions().position(current_locale).title("You are here!"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current_locale, 16.0f));

        this.setEventListener();
    }
}
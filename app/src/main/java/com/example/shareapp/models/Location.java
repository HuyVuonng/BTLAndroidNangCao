package com.example.shareapp.models;

import static android.content.Context.MODE_PRIVATE;

import static com.example.shareapp.controllers.constant.LocationConstant.LATITUDE;
import static com.example.shareapp.controllers.constant.LocationConstant.LONGITUDE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Location implements Serializable {
    private double longitude;
    private double latitude;

    public Location() {
    }

    public Location(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "Location{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    public static Location parse(String location) {
        String[] parts = location.split(",");
        return new Location(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }

    public static String toString(Location location) {
        return location.longitude + "," + location.latitude;
    }


    public static void setUserLocaleShared(Location location, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userInfor", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LONGITUDE, String.valueOf(location.getLongitude()) );
        editor.putString(LATITUDE, String.valueOf(location.getLatitude()));
        editor.apply();
    }


}

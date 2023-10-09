package com.example.shareapp.models;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class User {
    public String fullName;
    public String phoneNumber;
    public String address;
    public String email;
    public String uid;
    private static Context context;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String fullName, String phoneNumber, String address, String email, String uid) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static void setUserInfor(String fullName, String phoneNumber, String address, String email, String uid, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("userInfor", MODE_PRIVATE).edit();
        editor.putString("email", email);
        editor.putString("fullName", fullName);
        editor.putString("phoneNumber", phoneNumber);
        editor.putString("address", address);
        editor.putString("uid", uid);
        editor.apply();
    }

    public static User getUserInfor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userInfor", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String fullName = sharedPreferences.getString("fullName", "");
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        String address = sharedPreferences.getString("address", "");
        String uid = sharedPreferences.getString("uid", "");

        User user = new User(fullName, phoneNumber, address, email, uid);

        return user;
    }
}

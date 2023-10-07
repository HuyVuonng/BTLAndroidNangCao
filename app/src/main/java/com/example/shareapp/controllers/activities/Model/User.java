package com.example.shareapp.controllers.activities.Model;

public class User {
    public String fullName;
    public String phoneNumber;
    public String address;
    public String email;
    public String uid;

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
}

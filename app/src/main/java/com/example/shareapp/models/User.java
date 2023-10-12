package com.example.shareapp.models;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.shareapp.controllers.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {
    public String fullName;
    public String phoneNumber;
    public String address;
    public String email;
    public String uid;
    public String avata;
    public Boolean block;

    private static DatabaseReference mDatabase;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String fullName, String phoneNumber, String address, String email, String uid, String avata, Boolean block) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.uid = uid;
        this.avata = avata;
        this.block = block;
    }

    public User(String fullName, String phoneNumber, String address, String email, String uid) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.uid = uid;
    }

    public User(String fullName, String phoneNumber, String address, String email, String uid, String avata) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.uid = uid;
        this.avata = avata;
    }

    public String getAvata() {
        return avata;
    }

    public void setAvata(String avata) {
        this.avata = avata;
    }

    public Boolean getBlock() {
        return block;
    }

    public void setBlock(Boolean block) {
        this.block = block;
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


    public static void setUserInfor(String fullName, String phoneNumber, String address, String email, String uid, String avata, Boolean block, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("userInfor", MODE_PRIVATE).edit();
        editor.putString("email", email);
        editor.putString("fullName", fullName);
        editor.putString("phoneNumber", phoneNumber);
        editor.putString("address", address);
        editor.putString("uid", uid);
        editor.putString("avata", avata);
        editor.putBoolean("Block", block);
        editor.apply();
    }

    public static User getUserInfor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userInfor", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String fullName = sharedPreferences.getString("fullName", "");
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        String address = sharedPreferences.getString("address", "");
        String uid = sharedPreferences.getString("uid", "");
        String avata = sharedPreferences.getString("avata", "");
        Boolean block = sharedPreferences.getBoolean("block", false);

        return new User(fullName, phoneNumber, address, email, uid, avata, block);
    }

    public static void CreateNewUser(String fullName, String phoneNumber, String address, String email, String uid, String avata, Boolean block) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        User user = new User(fullName, phoneNumber, address, email, uid, avata, block);
        mDatabase.child("Users").child(uid).setValue(user);
    }

    public static void readDataUserFromFireBase(String uid, Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabase.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String fullName = String.valueOf(dataSnapshot.child("fullName").getValue());
                        String addressGet = String.valueOf(dataSnapshot.child("address").getValue());
                        String emailGet = String.valueOf(dataSnapshot.child("email").getValue());
                        String phoneNumberget = String.valueOf(dataSnapshot.child("phoneNumber").getValue());
                        String avataGet = String.valueOf(dataSnapshot.child("avata").getValue());
                        Boolean blockGet = (Boolean) dataSnapshot.child("block").getValue();


                        setUserInfor(fullName, phoneNumberget, addressGet, emailGet, uid, avataGet, blockGet, context);

                    } else {
                        Toast.makeText(context, "Không có người dùng này", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Không đọc được", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void updateUserInfor(String fullName, String phoneNumber, String address, String email, String uid, String avata, Boolean block, Context context) {
        User user = new User(fullName, phoneNumber, address, email, uid, avata, block);
        mDatabase.child(uid).setValue(user);
        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
    }
}

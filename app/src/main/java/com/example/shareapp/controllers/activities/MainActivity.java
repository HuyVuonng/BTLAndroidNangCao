package com.example.shareapp.controllers.activities;

import com.example.shareapp.controllers.methods.NavigationMethod;
import com.example.shareapp.models.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    TextView name, email, address;
    Button signOutBtn, userInfor;

    BottomNavigationView bnv_menu;

    private DatabaseReference mDatabase;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private void getViews() {
        this.bnv_menu = findViewById(R.id.main_bnv_menu);

    }

    private void setEventListener() {
        bnv_menu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.item_account) {
                Intent i = new Intent(MainActivity.this, UserInforActivity.class);
                startActivity(i);
                finish();
            }
            if (id == R.id.item_search) {
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(i);
                finish();
            }
            return true;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getViews();

        this.setEventListener();

        this.checkAuthenticateType();

        NavigationMethod.setNavigationMenu(this.bnv_menu, R.id.item_home);

    }

    private void checkAuthenticateType() {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        readDataUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        SharedPreferences editor1 = MainActivity.this.getSharedPreferences("data", MODE_PRIVATE);
        editor1.edit().clear().apply();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putString("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            editor.putBoolean("isLogin", true);
            editor.putString("type", "google");
            editor.apply();
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
            editor.putString("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            editor.putBoolean("isLogin", true);
            editor.putString("type", "EmailPassWord");
            editor.apply();
        }
    }


    private void readDataUser(String uid) {
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

                        User.setUserInfor(fullName, phoneNumberget, addressGet, emailGet, uid, MainActivity.this);

                    } else {
                        Toast.makeText(getApplicationContext(), "Không có người dùng này", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Không đọc được", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                SharedPreferences editor = MainActivity.this.getSharedPreferences("data", MODE_PRIVATE);
                editor.edit().clear().apply();

                SharedPreferences editor1 = MainActivity.this.getSharedPreferences("dataPass", MODE_PRIVATE);
                editor1.edit().clear().apply();

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
    }
}
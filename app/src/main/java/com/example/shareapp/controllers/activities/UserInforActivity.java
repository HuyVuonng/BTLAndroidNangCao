package com.example.shareapp.controllers.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareapp.R;
import com.example.shareapp.controllers.methods.NavigationMethod;
import com.example.shareapp.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInforActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    BottomNavigationView bnv_menu;

    EditText fullNameEdit, emailEdit, phoneNumberEdit, addressEdit;
    Button updateInforBTN;
    TextView logoutBTN;
    ProgressBar progressBar;
    GoogleSignInClient gsc;
    GoogleSignInOptions gso;

    private void getViews() {
        fullNameEdit = findViewById(R.id.fullNameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        phoneNumberEdit = findViewById(R.id.phoneNumberEdit);
        addressEdit = findViewById(R.id.addressEdit);
        progressBar = findViewById(R.id.progressBar4);
        updateInforBTN = findViewById(R.id.UpdateInforbtn);
        bnv_menu = findViewById(R.id.main_bnv_menu);
        logoutBTN=findViewById(R.id.activity_userInfor_tv_logout);
    }

    private void setEventListener() {
        bnv_menu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.item_home) {
                Intent i = new Intent(UserInforActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
            if (id == R.id.item_search) {
                Intent i = new Intent(UserInforActivity.this, SearchActivity.class);
                startActivity(i);
                finish();
            }
            return true;
        });
        updateInforBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                UpdateData(fullNameEdit.getText().toString().trim(),
                        phoneNumberEdit.getText().toString().trim(),
                        addressEdit.getText().toString().trim(),
                        emailEdit.getText().toString().trim(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        logoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences editor = UserInforActivity.this.getSharedPreferences("data", MODE_PRIVATE);
                editor.edit().clear().apply();

                SharedPreferences editor1 = UserInforActivity.this.getSharedPreferences("dataPass", MODE_PRIVATE);
                editor1.edit().clear().apply();

                Intent i = new Intent(UserInforActivity.this, LoginActivity.class);
                startActivity(i);
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infor);

        this.getViews();
        this.setEventListener();
        this.mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        this.readDataUser();
        NavigationMethod.setNavigationMenu(this.bnv_menu, R.id.item_account);


    }


    public void UpdateData(String fullName, String phoneNumber, String address, String email, String uid) {
        User user = new User(fullName, phoneNumber, address, email, uid);
        mDatabase.child(uid).setValue(user);
    }

    public void readDataUser() {
        String uid = User.getUserInfor(this.getApplicationContext()).uid;
        mDatabase.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String fullName = String.valueOf(dataSnapshot.child("fullName").getValue());
                        String addressGet = String.valueOf(dataSnapshot.child("address").getValue());
                        String emailGet = String.valueOf(dataSnapshot.child("email").getValue());
                        String phoneNumberGet = String.valueOf(dataSnapshot.child("phoneNumber").getValue());

                        fullNameEdit.setText(fullName);
                        addressEdit.setText(addressGet);
                        emailEdit.setText(emailGet);
                        phoneNumberEdit.setText(phoneNumberGet);
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
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                SharedPreferences editor = UserInforActivity.this.getSharedPreferences("data", MODE_PRIVATE);
                editor.edit().clear().apply();

                SharedPreferences editor1 = UserInforActivity.this.getSharedPreferences("dataPass", MODE_PRIVATE);
                editor1.edit().clear().apply();

                Intent i = new Intent(UserInforActivity.this, LoginActivity.class);
                startActivity(i);
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
    }
}


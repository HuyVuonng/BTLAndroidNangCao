package com.example.shareapp.controllers.activities;

import static com.example.shareapp.models.User.getUserInfor;
import static com.example.shareapp.models.User.readDataUserFromFireBase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shareapp.R;
import com.example.shareapp.controllers.fragments.PostAddSelectTypeBottomSheetDialog;
import com.example.shareapp.controllers.methods.NavigationMethod;
import com.example.shareapp.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInforActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    BottomNavigationView bnv_menu;
    FloatingActionButton btn_add_post;


    TextView fullNameView, emailView, phoneNumberView, addressView, logoutBTN;
    Button EditInforBTN;
    ProgressBar progressBar;
    ImageView avataView;
    GoogleSignInClient gsc;
    GoogleSignInOptions gso;

    private void getViews() {
        this.bnv_menu = findViewById(R.id.main_bnv_menu);
        this.btn_add_post = findViewById(R.id.post_fab_add_post);
        fullNameView = findViewById(R.id.fullNameView);
        emailView = findViewById(R.id.emailView);
        phoneNumberView = findViewById(R.id.phoneNumberView);
        addressView = findViewById(R.id.addressView);
        progressBar = findViewById(R.id.progressBar4);
        EditInforBTN = findViewById(R.id.EditInforbtn);
        logoutBTN = findViewById(R.id.activity_userInfor_tv_logout);
        avataView = findViewById(R.id.activity_userInfor_imgv_avata);
    }

    private void setEventListener() {
        bnv_menu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.item_home) {
                Intent i = new Intent(UserInforActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
            if (id == R.id.item_non_food) {
                Intent i = new Intent(UserInforActivity.this, NonFoodActivity.class);
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
        btn_add_post.setOnClickListener(v -> {
            PostAddSelectTypeBottomSheetDialog postAddSelectTypeBottomSheetDialog = new PostAddSelectTypeBottomSheetDialog();
            postAddSelectTypeBottomSheetDialog.show(getSupportFragmentManager(), "postAddSelectTypeBottomSheetDialog");
        });

        EditInforBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserInforActivity.this, UserInforUpdateActivity.class));
                finish();
            }
        });
        logoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infor);

        readDataUserFromFireBase(getUserInfor(this.getApplicationContext()).uid, UserInforActivity.this);
        this.getViews();
        this.setEventListener();
        setDataToView();
        NavigationMethod.setNavigationMenu(this.bnv_menu, R.id.item_account);
    }

    public void setDataToView() {
        fullNameView.setText(getUserInfor(UserInforActivity.this).getFullName().toString());
        addressView.setText(getUserInfor(UserInforActivity.this).getAddress().toString());
        emailView.setText(getUserInfor(UserInforActivity.this).getEmail().toString());
        phoneNumberView.setText(getUserInfor(UserInforActivity.this).getPhoneNumber().toString());
        if (getUserInfor(UserInforActivity.this).getAvata() != "") {
            Glide.with(UserInforActivity.this).load(getUserInfor(UserInforActivity.this).getAvata().toString()).into(avataView);
        }
    }

    public void LogOut() {
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


package com.example.shareapp.controllers.activities;

import static com.example.shareapp.models.User.readDataUserFromFireBase;

import com.example.shareapp.controllers.fragments.FoodFragment;
import com.example.shareapp.controllers.fragments.NonFoodFragment;
import com.example.shareapp.controllers.fragments.PostAddSelectTypeBottomSheetDialog;
import com.example.shareapp.controllers.fragments.SearchFragment;
import com.example.shareapp.controllers.fragments.UserInforFragment;
import com.example.shareapp.controllers.methods.NavigationMethod;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.shareapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    public static final String NAME_TYPE = "TypePost";
    public static final String TYPE_FOOD = "Food";
    public static final String TYPE_NON_FOOD = "Non-Food";
    BottomNavigationView bnv_menu;
    FloatingActionButton btn_add_post;
    ViewPager viewPager;
    FrameLayout frameLayout;
    private DatabaseReference mDatabase;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private void getViews() {
        this.bnv_menu = findViewById(R.id.main_bnv_menu);
        this.btn_add_post = findViewById(R.id.post_fab_add_post);
//        viewPager= findViewById(R.id.mainViewPager);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    private void setEventListener() {
        bnv_menu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.item_home) {
                replaceFragment(new FoodFragment());
            }
            if (id == R.id.item_non_food) {
                replaceFragment(new NonFoodFragment());
            }
            if (id == R.id.item_account) {
                replaceFragment(new UserInforFragment());

            }
            if (id == R.id.item_search) {
                replaceFragment(new SearchFragment());

            }
            return true;
        });
        btn_add_post.setOnClickListener(v -> {
            PostAddSelectTypeBottomSheetDialog postAddSelectTypeBottomSheetDialog = new PostAddSelectTypeBottomSheetDialog();
            postAddSelectTypeBottomSheetDialog.show(getSupportFragmentManager(), "postAddSelectTypeBottomSheetDialog");
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
        readDataUserFromFireBase(FirebaseAuth.getInstance().getCurrentUser().getUid(), MainActivity.this);
        checkFragment();
    }

    private void checkFragment() {
        SharedPreferences sharedPreferences = getSharedPreferences("fragment", MODE_PRIVATE);
        String fragment = sharedPreferences.getString("fragment", "home");
        if (fragment.equals("userInfor")) {
            replaceFragment(new UserInforFragment());
            NavigationMethod.setNavigationMenu(this.bnv_menu, R.id.item_account);
            sharedPreferences.edit().clear().commit();
        } else {
            replaceFragment(new FoodFragment());
        }
    }

    private void checkAuthenticateType() {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        readDataUserFromFireBase(FirebaseAuth.getInstance().getCurrentUser().getUid(), MainActivity.this);
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

}
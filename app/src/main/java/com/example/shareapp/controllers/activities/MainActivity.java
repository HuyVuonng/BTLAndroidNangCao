package com.example.shareapp.controllers.activities;

import static com.example.shareapp.models.User.getUserInfor;
import static com.example.shareapp.models.User.readDataUserFromFireBase;

import com.example.shareapp.controllers.fragments.FragmentFood;
import com.example.shareapp.controllers.fragments.FragmentNonFood;
import com.example.shareapp.controllers.fragments.PostAddSelectTypeBottomSheetDialog;
import com.example.shareapp.controllers.methods.NavigationMethod;
import com.example.shareapp.models.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.shareapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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


public class MainActivity extends AppCompatActivity {
    public static final int FRAGMENT_FOOD = 0;
    public static final int FRAGMENT_NON_FOOD = 1;
    public int currentFragment = FRAGMENT_FOOD;
    BottomNavigationView bnv_menu;
    FloatingActionButton btn_add_post;

    private DatabaseReference mDatabase;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    BottomNavigationView navigationView;

    private void getViews() {
        this.bnv_menu = findViewById(R.id.main_bnv_menu);
        this.btn_add_post = findViewById(R.id.post_fab_add_post);
        navigationView = findViewById(R.id.main_bnv_menu);
    }

    private void setEventListener() {
        bnv_menu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.item_home) {
                if(currentFragment != FRAGMENT_FOOD) {
                    replaceFragment(new FragmentFood());
                    currentFragment = FRAGMENT_FOOD;
                }
            }
            else if (id == R.id.item_non_food) {
                if(currentFragment != FRAGMENT_NON_FOOD) {
                    replaceFragment(new FragmentNonFood());
                    currentFragment = FRAGMENT_NON_FOOD;
                }
            }
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

        navigationView.getMenu().findItem(R.id.item_home).setChecked(true);
        replaceFragment(new FragmentFood());
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

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.list_fragment, fragment);
        transaction.commit();
    }

}
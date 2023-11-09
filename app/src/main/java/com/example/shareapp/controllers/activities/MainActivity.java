package com.example.shareapp.controllers.activities;

import static com.example.shareapp.models.User.getUserInfor;
import static com.example.shareapp.models.User.readDataUserFromFireBase;

import com.bumptech.glide.Glide;
import com.example.shareapp.controllers.fragments.FoodFragment;
import com.example.shareapp.controllers.fragments.NonFoodFragment;
import com.example.shareapp.controllers.fragments.PostAddSelectTypeBottomSheetDialog;
import com.example.shareapp.controllers.fragments.SearchFragment;
import com.example.shareapp.controllers.fragments.UserInforFragment;
import com.example.shareapp.controllers.methods.KeyBoardMethod;
import com.example.shareapp.controllers.methods.NavigationMethod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    public static final String NAME_TYPE = "TypePost";
    public static final String TYPE_FOOD = "Food";
    public static final String TYPE_NON_FOOD = "Non-Food";
    public static final String ACTION_NAME = "action";
    public static final String ACTION_CREATE_POST = "create_post";
    public static final String ACTION_UPDATE_POST = "update_post";
    public static final String MY_POST = "myPost";
    BottomNavigationView bnv_menu;
    FloatingActionButton btn_add_post;
    ViewPager viewPager;
    FrameLayout frameLayout;
    boolean passWordVisible, passWordOldVisible, passWordRepeatVisible;

    DrawerLayout drawerLayout;
    ImageButton sideBarBtn;
    private DatabaseReference mDatabase;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    ImageView avatarNav;
    TextView nameUserNav;
    NavigationView navView;

    private void getViews() {
        this.bnv_menu = findViewById(R.id.main_bnv_menu);
        this.btn_add_post = findViewById(R.id.post_fab_add_post);
        sideBarBtn = findViewById(R.id.side_bar_btn);
        drawerLayout = findViewById(R.id.DrawerLayout);
        navView = findViewById(R.id.navView);
        navView.bringToFront();
        avatarNav = navView.getHeaderView(0).findViewById(R.id.avatarNav);
        nameUserNav = navView.getHeaderView(0).findViewById(R.id.nameUserNav);
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
        sideBarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameUserNav.setText(getUserInfor(getApplicationContext()).getFullName());
                if (getUserInfor(getApplicationContext()).getAvata().length() > 0) {
                    Glide.with(getApplicationContext()).load(getUserInfor(getApplicationContext()).getAvata().toString()).into(avatarNav);
                }
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_manager_post) {
                startActivity(new Intent(getApplicationContext(), ManagerPost.class));
            }
            if (id == R.id.nav_notify) {
                navView.getMenu().findItem(R.id.nav_notify).setChecked(true);
                replaceFragment(new NonFoodFragment());
                Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_LONG).show();
            }
            if (id == R.id.nav_location) {
                replaceFragment(new UserInforFragment());
            }
            if (id == R.id.nav_edit_profile) {
                startActivity(new Intent(getApplicationContext(), UserInforUpdateActivity.class));
                finish();
            }
            if (id == R.id.nav_change_password) {
                SharedPreferences sharedPreferences = getSharedPreferences("dataPass", MODE_PRIVATE);
                String Pass = sharedPreferences.getString("password", "");
                if (Pass.length() >= 6) {
                    showDialog(Pass);
                } else {
                    Toast.makeText(getApplicationContext(), "Bạn đang đăng nhập bằng tài khoản Google nên không dùng được chức năng này", Toast.LENGTH_LONG).show();
                }
            }
            if (id == R.id.nav_logout) {
                LogOut();
            }
            drawerLayout.closeDrawer(GravityCompat.END);
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
        readDataUserFromFireBase(FirebaseAuth.getInstance().getCurrentUser().getUid(), MainActivity.this);
        checkFragment();
    }

    private void checkFragment() {
        String fragment = getIntent().getStringExtra("fragment");
        if (fragment != null && fragment.equals("userInfor")) {
            replaceFragment(new UserInforFragment());
            NavigationMethod.setNavigationMenu(this.bnv_menu, R.id.item_account);
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

    public void LogOut() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                SharedPreferences editor = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
                editor.edit().clear().apply();

                SharedPreferences editor1 = getApplicationContext().getSharedPreferences("dataPass", MODE_PRIVATE);
                editor1.edit().clear().apply();

                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
    }


    public void showDialog(String Pass) {


        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_pass);
        dialog.show();

        EditText password = dialog.findViewById(R.id.passwordChange);
        EditText passwordOld = dialog.findViewById(R.id.passwordOld);
        EditText repassword = dialog.findViewById(R.id.repassword);
        Button changePassBtn = dialog.findViewById(R.id.changePasswordBtn);
        Button backBtn = dialog.findViewById(R.id.backbtn);

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= password.getRight() - password.getCompoundDrawables()[right].getBounds().width() - 40) {
                        int selection = password.getSelectionEnd();
                        if (passWordVisible) {
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_info_24, 0, R.drawable.baseline_visibility_off_24, 0);
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passWordVisible = false;
                        } else {
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_info_24, 0, R.drawable.baseline_visibility_24, 0);
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passWordVisible = true;
                        }
                        password.setSelection(selection);
                        return true;
                    }
                }

                return false;
            }
        });

        passwordOld.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= passwordOld.getRight() - passwordOld.getCompoundDrawables()[right].getBounds().width() - 40) {
                        int selection = passwordOld.getSelectionEnd();
                        if (passWordOldVisible) {
                            passwordOld.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_info_24, 0, R.drawable.baseline_visibility_off_24, 0);
                            passwordOld.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passWordOldVisible = false;
                        } else {
                            passwordOld.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_info_24, 0, R.drawable.baseline_visibility_24, 0);
                            passwordOld.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passWordOldVisible = true;
                        }
                        passwordOld.setSelection(selection);
                        return true;
                    }
                }

                return false;
            }
        });

        repassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= repassword.getRight() - repassword.getCompoundDrawables()[right].getBounds().width() - 40) {
                        int selection = repassword.getSelectionEnd();
                        if (passWordRepeatVisible) {
                            repassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_info_24, 0, R.drawable.baseline_visibility_off_24, 0);
                            repassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passWordRepeatVisible = false;
                        } else {
                            repassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_info_24, 0, R.drawable.baseline_visibility_24, 0);
                            repassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passWordRepeatVisible = true;
                        }
                        repassword.setSelection(selection);
                        return true;
                    }
                }

                return false;
            }
        });
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (password.getText().toString().trim().length() < 6) {
                    password.setError("Mật khẩu phải có ít nhất 6 ký tự");
                    return;
                }

                if (password.getText().toString().trim().equals(repassword.getText().toString().trim())) {
                    if (Pass.equals(passwordOld.getText().toString().trim())) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential authCredential = EmailAuthProvider.getCredential(getUserInfor(getApplicationContext()).getEmail(), Pass);
                        user.reauthenticate(authCredential)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        user.updatePassword(password.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(), "Cập nhập thành công", Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Cập nhập thất bại", Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Cập nhập thất bại", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                });
                    } else {
                        passwordOld.setError("Mật khẩu không đúng");
                    }
                } else {
                    repassword.setError("Mật khẩu không trùng khớp, hãy nhập lại");
                }

            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] sourceCoordinates = new int[2];
            v.getLocationOnScreen(sourceCoordinates);
            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                KeyBoardMethod.hideKeyboard(this);
            }

        }
        return super.dispatchTouchEvent(ev);
    }


}
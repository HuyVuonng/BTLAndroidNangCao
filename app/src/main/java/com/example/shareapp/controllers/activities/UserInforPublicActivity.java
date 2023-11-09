package com.example.shareapp.controllers.activities;

import static com.example.shareapp.controllers.constant.AuthenticateConstant.PEMISSION_CALL_CODE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shareapp.R;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserInforPublicActivity extends AppCompatActivity {
    ImageView avatar;
    TextView name, address, email, phone, introduce, numberOfPost, viewAllPost;
    FloatingActionButton callBtn, sendMail;
    private ImageButton imbBackPage;

    int soBaiDang = 0;

    private void getViews() {
        avatar = findViewById(R.id.activity_userInforPublic_imgv_avata);
        name = findViewById(R.id.activity_userInforPublic_tv_fullNameView);
        address = findViewById(R.id.activity_userInforPublic_tv_addressView);
        email = findViewById(R.id.activity_userInforPublic_tv_emailView);
        phone = findViewById(R.id.activity_userInforPublic_tv_phoneNumberView);
        introduce = findViewById(R.id.activity_userInforPublic_tv_Introduce);
//        introduce.setMovementMethod(ScrollingMovementMethod.getInstance());
        numberOfPost = findViewById(R.id.numberOfPost);
        imbBackPage = findViewById(R.id.imb_back_page);
        callBtn = findViewById(R.id.activity_userInforPublic_fb_floatingActionButton);
        sendMail = findViewById(R.id.activity_userInforPublic_fb_floatingActionButtonSendMail);
        viewAllPost = findViewById(R.id.viewAllPost);
    }

    private void getUserInforByIDUserPost(String uid) {
        new User().getUserById(uid, new User.IUserDataReceivedListener() {
            @Override
            public Boolean onUserDataReceived(User user) {
                if (user != null) {
                    if (!TextUtils.isEmpty(user.getAvata())) {
                        Glide.with(UserInforPublicActivity.this).load(user.getAvata()).into(avatar);
                    }
                    name.setText(user.getFullName());
                    address.setText(user.getAddress());
                    email.setText(user.getEmail());

                    if (user.getIntroduce() == null || user.getIntroduce().length() == 0) {
                        introduce.setText("Chưa có giới thiệu về tài khoản này");
                    } else {
                        introduce.setText(user.getIntroduce());

                    }
                    if (user.getShowPhoneNumberPublic() != null) {
                        if (user.getShowPhoneNumberPublic()) {
                            phone.setText(user.getPhoneNumber());
                        } else {
                            phone.setVisibility(View.GONE);
                            callBtn.setVisibility(View.GONE);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sendMail.getLayoutParams();
                            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        }
                    } else {
                        phone.setVisibility(View.GONE);
                        callBtn.setVisibility(View.GONE);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sendMail.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    }

                }
                return null;
            }
        });
    }

    private void getNumberOfPost(String uid) {
        Post.getFirebaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getUserId().equals(uid)) {
                        soBaiDang++;
                    }
                }
                numberOfPost.setText("Tổng số bài đăng: " + soBaiDang);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setEventListener() {
        imbBackPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UserInforPublicActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PEMISSION_CALL_CODE);
                } else {
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse("tel:" + phone.getText().toString()));
                    startActivity(i);
                }
            }
        });

        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email.getText().toString()});
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                intent.setType("message/rfc822");
                startActivity(intent);
            }
        });
        viewAllPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManagerPost.class);
                String uid = getIntent().getStringExtra("uid");
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PEMISSION_CALL_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:" + phone.getText().toString()));
            startActivity(i);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infor_public);
        getViews();
        String idUser = getIntent().getStringExtra("uid");
        getUserInforByIDUserPost(idUser);
        getNumberOfPost(idUser);
        setEventListener();
    }
}
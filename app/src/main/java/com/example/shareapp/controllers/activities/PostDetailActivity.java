package com.example.shareapp.controllers.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shareapp.R;
import com.example.shareapp.controllers.methods.DateTimeMethod;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.User;

public class PostDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton imbBackPage;
    private ImageView imvImagePost, cimvImagePoster;
    private TextView tvTitlePage, tvFullNamePoster, tvTitlePost, tvCreatedAt, tvQuantity, tvDescription;
    private Button btnRequestPost;
    private Post mPost;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        getViews();
        getDataIntent();
        setEventListener();
    }

    private void getViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imbBackPage = findViewById(R.id.imb_back_page);
        tvTitlePage = findViewById(R.id.tv_title_page);
        imvImagePost = findViewById(R.id.iv_image_post);
        cimvImagePoster = findViewById(R.id.cimg_image_poster);
        tvFullNamePoster = findViewById(R.id.tv_full_name_poster);
        tvTitlePost = findViewById(R.id.tv_title_post);
        tvCreatedAt = findViewById(R.id.tv_created_at);
        tvQuantity = findViewById(R.id.tv_quantity_product);
        tvDescription = findViewById(R.id.tv_description);
        btnRequestPost = findViewById(R.id.btn_request_post);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void getDataIntent() {
        Intent intent = getIntent();
        mPost = (Post) intent.getSerializableExtra("item_post");

        if (!TextUtils.isEmpty(mPost.getImage()))
            Glide.with(PostDetailActivity.this).load(mPost.getImage()).into(imvImagePost);
        tvCreatedAt.setText("Từ " + DateTimeMethod.timeDifference(mPost.getCreatedAt()));
        tvTitlePost.setText(mPost.getTitle());
        tvQuantity.setText(String.valueOf(mPost.getCount()));
        tvDescription.setText(mPost.getDescription());
        new User().getUserById(mPost.getUserId(), new User.IUserDataReceivedListener() {
            @Override
            public Boolean onUserDataReceived(User user) {
                if (user != null) {
                    if (user.getAvata() != null) {
                        Glide.with(PostDetailActivity.this).load(user.getAvata()).into(cimvImagePoster);
                    }
                    tvFullNamePoster.setText(user.getFullName() + " đang cho đi");
                }
                return null;
            }
        });
    }

    private void setEventListener() {
        tvTitlePage.setText(mPost.getTitle());
        imbBackPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnRequestPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PostDetailActivity.this, "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PostDetailActivity.this, MainActivity.class));
            }
        });

        cimvImagePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                mPost = (Post) intent.getSerializableExtra("item_post");
                Intent i = new Intent(getApplicationContext(), UserInforPublicActivity.class);
                i.putExtra("uid", mPost.getUserId());
                startActivity(i);
            }
        });
    }
}
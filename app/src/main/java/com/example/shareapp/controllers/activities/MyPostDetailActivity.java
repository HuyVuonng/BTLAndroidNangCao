package com.example.shareapp.controllers.activities;

import static com.example.shareapp.controllers.activities.MainActivity.ACTION_NAME;
import static com.example.shareapp.controllers.activities.MainActivity.ACTION_UPDATE_POST;
import static com.example.shareapp.controllers.activities.MainActivity.MY_POST;
import static com.example.shareapp.controllers.activities.MainActivity.NAME_TYPE;

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

public class MyPostDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton imbBackPage;
    private ImageView imvImagePost, cimvImagePoster;
    private TextView tvTitlePage, tvFullNamePoster, tvTitlePost, tvCreatedAt, tvQuantity, tvDescription;
    private Button btnUpdate, btnDelete;
    private Post mPost;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_detail);

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
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void getDataIntent() {
        Intent intent = getIntent();
        mPost = (Post) intent.getSerializableExtra("item_post");

        if(!TextUtils.isEmpty(mPost.getImage()))
            Glide.with(MyPostDetailActivity.this).load(mPost.getImage()).into(imvImagePost);
        tvCreatedAt.setText("Từ " + DateTimeMethod.timeDifference(mPost.getCreatedAt()));
        tvTitlePost.setText(mPost.getTitle());
        tvQuantity.setText(String.valueOf(mPost.getCount()));
        tvDescription.setText(mPost.getDescription());
        new User().getUserById(mPost.getUserId(), new User.IUserDataReceivedListener() {
            @Override
            public void onUserDataReceived(User user) {
                if(user != null) {
                    if(user.getAvata() != null) {
                        Glide.with(MyPostDetailActivity.this).load(user.getAvata()).into(cimvImagePoster);
                    }
                    tvFullNamePoster.setText(user.getFullName() + " đang cho đi");
                }
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
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPostDetailActivity.this, CreatePostActivity.class);
                intent.putExtra(ACTION_NAME, ACTION_UPDATE_POST);
                intent.putExtra(MY_POST, mPost);
                intent.putExtra(NAME_TYPE, mPost.getType());
                startActivity(intent);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
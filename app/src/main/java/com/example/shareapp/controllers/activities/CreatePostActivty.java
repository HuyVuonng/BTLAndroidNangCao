package com.example.shareapp.controllers.activities;

import static com.example.shareapp.controllers.activities.MainActivity.NAME_TYPE;
import static com.example.shareapp.models.User.getUserInfor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.shareapp.R;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreatePostActivty extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton imbImagePost;
    private TextView tvErrorImage;
    private EditText edtTitle, edtDescription, edtQuantity;
    private Button btnSubmit;
    private String typePost;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;
    private Uri uriImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        getViews();
        getDataIntent();
        setEventListener();
    }

    private void getViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imbImagePost = findViewById(R.id.imv_image_post);
        tvErrorImage = findViewById(R.id.tv_error_no_image);
        edtTitle = findViewById(R.id.edt_title_post);
        edtDescription = findViewById(R.id.edt_description);
        edtQuantity = findViewById(R.id.edt_quantity);
        btnSubmit = findViewById(R.id.btn_submit);
        progressDialog = new ProgressDialog(this);

        database = FirebaseDatabase.getInstance();
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        typePost = intent.getStringExtra(NAME_TYPE);
    }

    private void setEventListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imbImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(CreatePostActivty.this)
                        .cropSquare()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(validateData()) {
                    Post post = new Post();
                    post.setPostId(UUID.randomUUID().toString());
                    post.setUser(getUserInfor(CreatePostActivty.this));
                    post.setTitle(edtTitle.getText().toString());
                    post.setType(typePost);
                    post.setCount(Integer.parseInt(edtQuantity.getText().toString()));
                    post.setCreatedAt(System.currentTimeMillis());
                    post.setUpdatedAt(System.currentTimeMillis());
                    post.setDelete(false);
                    post.setLocation("");

                    createPost(post);
                }
            }
        });
    }

    private boolean validateData() {
        boolean isOk = true;
        if(uriImage == null) {
            tvErrorImage.setText("Vui lòng chọn ảnh");
            isOk = false;
        }
        else {
            tvErrorImage.setText("");
        }

        if(TextUtils.isEmpty(edtTitle.getText().toString())) {
            edtTitle.setError("Tiêu đề không được để trống");
            isOk = false;
        }

        if(TextUtils.isEmpty(edtDescription.getText().toString())) {
            edtDescription.setError("Mô tả không được để trống");
            isOk = false;
        }

        return isOk;
    }

    private void createPost(Post post) {
        DatabaseReference myRef = database.getReference("Posts");
        progressDialog.show();

        // Đẩy ảnh lên firebase
        uploadImageToFirebase(post, myRef);

    }

    private void resetData() {
        edtTitle.setText("");
        edtDescription.setText("");
        edtQuantity.setText("");
        imbImagePost.setImageResource(R.drawable.no_img);
        uriImage = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            uriImage = data.getData();
            imbImagePost.setImageURI(uriImage);
        }
    }

    private void uploadImageToFirebase(Post post, DatabaseReference myRef) {
        StorageReference myStore = FirebaseStorage.getInstance().getReference().child("Images/Post").child(uriImage.getLastPathSegment());
        myStore.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri resultUri = uriTask.getResult();
                String urlImageString = resultUri.toString();

                post.setImage(urlImageString);

                String pathObject = String.valueOf(post.getPostId());
                myRef.child(pathObject).setValue(post, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        progressDialog.dismiss();
                        resetData();
                        Toast.makeText(CreatePostActivty.this, "Create post successed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Lỗi upload ảnh", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

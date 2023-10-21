package com.example.shareapp.controllers.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shareapp.R;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.User;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreatePostActivty extends AppCompatActivity {
    private EditText edtTitle, edtDescription, edtQuantity;
    private Button btnSubmit;
    private String typePost;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        getViews();
        getDataIntent();
        setEventListener();
    }

    private void getViews() {
        edtTitle = findViewById(R.id.edt_title_post);
        edtDescription = findViewById(R.id.edt_description);
        edtQuantity = findViewById(R.id.edt_quantity);
        btnSubmit = findViewById(R.id.btn_submit);
        progressDialog = new ProgressDialog(this);
    }

    private void setEventListener() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Post post = new Post();
                post.setPostId(UUID.randomUUID());
                post.setUserId(null);
                post.setTitle(edtTitle.getText().toString());
                post.setType(typePost);
                post.setCount(Integer.parseInt(edtQuantity.getText().toString()));
                post.setCreatedAt(LocalDateTime.now());
                post.setUpdatedAt(LocalDateTime.now());
                createPost(post);
            }
        });
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        typePost = intent.getStringExtra("TypePost");
    }

    private void createPost(Post post) {
        DatabaseReference myRef = database.getReference("Posts");

        String pathObject = String.valueOf(post.getPostId());
        progressDialog.show();
        myRef.child(pathObject).setValue(post, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                progressDialog.dismiss();
                Toast.makeText(CreatePostActivty.this, "Create Post sucessed", Toast.LENGTH_SHORT).show();
                resetData();
            }
        });
    }

    private void resetData() {
        edtTitle.setText("");
        edtDescription.setText("");
        edtQuantity.setText("");
    }
}

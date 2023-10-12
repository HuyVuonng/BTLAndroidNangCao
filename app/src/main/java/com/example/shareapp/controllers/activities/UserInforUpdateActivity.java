package com.example.shareapp.controllers.activities;

import static com.example.shareapp.models.User.getUserInfor;
import static com.example.shareapp.models.User.setUserInfor;
import static com.example.shareapp.models.User.updateUserInfor;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shareapp.R;
import com.example.shareapp.controllers.fragments.PostAddSelectTypeBottomSheetDialog;
import com.example.shareapp.controllers.methods.NavigationMethod;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UserInforUpdateActivity extends AppCompatActivity {

    EditText fullNameEdit, emailEdit, phoneNumberEdit, addressEdit;

    Button backBTN, updateBTN;

    ImageView avataEdit;
    ImageButton folderBtn;
    ProgressBar progressBar;

    Uri uri;
    String oldImgUri, imgUrl;

    DatabaseReference databaseReference;
    StorageReference storageReference;


    private void getViews() {
        backBTN = findViewById(R.id.activity_userInfor_update_btn_back);
        fullNameEdit = findViewById(R.id.activity_userInfor_update_edt_fullNameEdit);
        emailEdit = findViewById(R.id.activity_userInfor_update_edt_emailEdit);
        phoneNumberEdit = findViewById(R.id.activity_userInfor_update_edt_phoneNumberEdit);
        addressEdit = findViewById(R.id.activity_userInfor_update_edt_addressEdit);
        updateBTN = findViewById(R.id.activity_userInfor_update_btn_update);
        avataEdit = findViewById(R.id.activity_userInfor_update_imgv_avata);
        progressBar = findViewById(R.id.progressBar4);
        folderBtn = findViewById(R.id.activity_userInfor_update_btn_open_camera);
    }

    private void setEventListener() {

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            avataEdit.setImageURI(uri);
                        } else {
                            Toast.makeText(UserInforUpdateActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UserInforActivity.class));
                finish();
            }
        });

        folderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });


        updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String fullName = fullNameEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String phoneNumber = phoneNumberEdit.getText().toString().trim();
                String address = addressEdit.getText().toString().trim();


                if (TextUtils.isEmpty(fullName)) {
                    fullNameEdit.setError("Nhập tên");
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    phoneNumberEdit.setError("Nhập số điện thoại");
                    return;
                }
                if (phoneNumber.length() != 10) {
                    phoneNumberEdit.setError("Nhập số điện thoại sai");
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    addressEdit.setError("Nhập địa chỉ");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailEdit.setError("Nhập Email");
                    return;
                }
                if (uri != null) {
                    storageReference = FirebaseStorage.getInstance().getReference().child("Images").child(uri.getLastPathSegment());
                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isComplete()) ;
                            Uri urlImage = uriTask.getResult();
                            imgUrl = urlImage.toString();
                            if(oldImgUri!=""){
                                StorageReference reference= FirebaseStorage.getInstance().getReferenceFromUrl(oldImgUri);
                                reference.delete();
                            }
                            updateUserInfor(fullName, phoneNumber, address, email, getUserInfor(UserInforUpdateActivity.this).getUid().toString(),
                                    imgUrl, getUserInfor(UserInforUpdateActivity.this).getBlock(), getApplicationContext());
                            progressBar.setVisibility(View.INVISIBLE);
                            setUserInfor(fullName, phoneNumber, address, email,
                                    getUserInfor(UserInforUpdateActivity.this).getUid().toString(),
                                    imgUrl, getUserInfor(UserInforUpdateActivity.this).getBlock(), getApplicationContext());
                            startActivity(new Intent(getApplicationContext(), UserInforActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "lỗi", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    updateUserInfor(fullName, phoneNumber, address, email, getUserInfor(UserInforUpdateActivity.this).getUid().toString(),
                            oldImgUri, getUserInfor(UserInforUpdateActivity.this).getBlock(), getApplicationContext());
                    progressBar.setVisibility(View.INVISIBLE);
                    setUserInfor(fullName, phoneNumber, address, email,
                            getUserInfor(UserInforUpdateActivity.this).getUid().toString(),
                            getUserInfor(UserInforUpdateActivity.this).getAvata().toString(), getUserInfor(UserInforUpdateActivity.this).getBlock(), getApplicationContext());
                    startActivity(new Intent(getApplicationContext(), UserInforActivity.class));
                    finish();
                }
            }
        });
    }

    private void setUserInforToView() {
        fullNameEdit.setText(getUserInfor(UserInforUpdateActivity.this).getFullName().toString());
        emailEdit.setText(getUserInfor(UserInforUpdateActivity.this).getEmail().toString());
        phoneNumberEdit.setText(getUserInfor(UserInforUpdateActivity.this).getPhoneNumber().toString());
        addressEdit.setText(getUserInfor(UserInforUpdateActivity.this).getAddress().toString());
        if (getUserInfor(UserInforUpdateActivity.this).getAvata() != "") {
            Glide.with(UserInforUpdateActivity.this).load(getUserInfor(UserInforUpdateActivity.this).getAvata().toString()).into(avataEdit);
        }
        oldImgUri = getUserInfor(UserInforUpdateActivity.this).getAvata().toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infor_update);

        this.getViews();
        setUserInforToView();
        this.setEventListener();


    }
}
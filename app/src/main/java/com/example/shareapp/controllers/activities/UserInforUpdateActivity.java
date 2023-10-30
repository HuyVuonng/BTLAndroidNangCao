package com.example.shareapp.controllers.activities;

import static com.example.shareapp.models.User.getUserInfor;
import static com.example.shareapp.models.User.setUserInfor;
import static com.example.shareapp.models.User.updateUserInfor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shareapp.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class UserInforUpdateActivity extends AppCompatActivity {

    EditText fullNameEdit, emailEdit, phoneNumberEdit, addressEdit;

    Button updateBTN;

    ImageView avataEdit;
    ImageButton backBTN;
    ProgressBar progressBar;

    Uri uri;
    String oldImgUri, imgUrl;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    ImageButton changeAvatarbtn;


    private void getViews() {
        backBTN = findViewById(R.id.activity_userInfor_update_btn_back);
        fullNameEdit = findViewById(R.id.activity_userInfor_update_edt_fullNameEdit);
        emailEdit = findViewById(R.id.activity_userInfor_update_edt_emailEdit);
        phoneNumberEdit = findViewById(R.id.activity_userInfor_update_edt_phoneNumberEdit);
        addressEdit = findViewById(R.id.activity_userInfor_update_edt_addressEdit);
        updateBTN = findViewById(R.id.activity_userInfor_update_btn_update);
        avataEdit = findViewById(R.id.activity_userInfor_update_imgv_avata);
        progressBar = findViewById(R.id.progressBar4);
        changeAvatarbtn = findViewById(R.id.changeAvatarbtn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            assert data != null;
            uri = data.getData();
            avataEdit.setImageURI(uri);
        } else {
            Toast.makeText(UserInforUpdateActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void setEventListener() {
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("fragment", Context.MODE_PRIVATE).edit();
                editor.putString("fragment", "userInfor");
                editor.apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        avataEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(UserInforUpdateActivity.this)
                        .cropSquare()                //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
//                Intent photoPicker = new Intent(Intent.ACTION_PICK);
//                photoPicker.setType("image/*");
//                activityResultLauncher.launch(photoPicker);
            }
        });


        changeAvatarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(UserInforUpdateActivity.this)
                        .cropSquare()                //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
//                Intent photoPicker = new Intent(Intent.ACTION_PICK);
//                photoPicker.setType("image/*");
//                activityResultLauncher.launch(photoPicker);
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
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    phoneNumberEdit.setError("Nhập số điện thoại");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if (phoneNumber.length() != 10) {
                    phoneNumberEdit.setError("Nhập số điện thoại sai");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    addressEdit.setError("Nhập địa chỉ");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailEdit.setError("Nhập Email");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if (uri != null && uri.getLastPathSegment() != null) {
                    storageReference = FirebaseStorage.getInstance().getReference().child("Images").child(uri.getLastPathSegment());
                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isComplete()) ;
                            Uri urlImage = uriTask.getResult();
                            imgUrl = urlImage.toString();
                            if (oldImgUri.length() > 0) {
                                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImgUri);
                                reference.delete();
                            }
                            updateUserInfor(fullName, phoneNumber, address, email, getUserInfor(UserInforUpdateActivity.this).getUid(),
                                    imgUrl, getUserInfor(UserInforUpdateActivity.this).getBlock(), getApplicationContext());
                            progressBar.setVisibility(View.INVISIBLE);
                            setUserInfor(fullName, phoneNumber, address, email,
                                    getUserInfor(UserInforUpdateActivity.this).getUid(),
                                    imgUrl, getUserInfor(UserInforUpdateActivity.this).getBlock(), getApplicationContext());
                            SharedPreferences.Editor editor = getSharedPreferences("fragment", Context.MODE_PRIVATE).edit();
                            editor.putString("fragment", "userInfor");
                            editor.apply();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "lỗi", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    updateUserInfor(fullName, phoneNumber, address, email, getUserInfor(UserInforUpdateActivity.this).getUid(),
                            oldImgUri, getUserInfor(UserInforUpdateActivity.this).getBlock(), getApplicationContext());
                    progressBar.setVisibility(View.INVISIBLE);
                    setUserInfor(fullName, phoneNumber, address, email,
                            getUserInfor(UserInforUpdateActivity.this).getUid(),
                            getUserInfor(UserInforUpdateActivity.this).getAvata(), getUserInfor(UserInforUpdateActivity.this).getBlock(), getApplicationContext());
                    SharedPreferences.Editor editor = getSharedPreferences("fragment", Context.MODE_PRIVATE).edit();
                    editor.putString("fragment", "userInfor");
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        });
    }

    private void setUserInforToView() {
        fullNameEdit.setText(getUserInfor(UserInforUpdateActivity.this).getFullName());
        emailEdit.setText(getUserInfor(UserInforUpdateActivity.this).getEmail());
        phoneNumberEdit.setText(getUserInfor(UserInforUpdateActivity.this).getPhoneNumber());
        addressEdit.setText(getUserInfor(UserInforUpdateActivity.this).getAddress());
        if (!Objects.equals(getUserInfor(UserInforUpdateActivity.this).getAvata(), "")) {
            Glide.with(UserInforUpdateActivity.this).load(getUserInfor(UserInforUpdateActivity.this).getAvata()).into(avataEdit);
        }
        oldImgUri = getUserInfor(UserInforUpdateActivity.this).getAvata();
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
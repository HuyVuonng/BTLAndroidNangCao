package com.example.shareapp.controllers.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.shareapp.R;
import com.example.shareapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivities extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText fullName,phoneNumber,address, email,passWord;
    Button registerbtn;
    ProgressBar progressBar;
    boolean passWordVisible;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        anhxa();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences editor= RegisterActivities.this.getSharedPreferences("data",MODE_PRIVATE);
        editor.edit().clear().commit();

        SharedPreferences editor1= RegisterActivities.this.getSharedPreferences("dataPass",MODE_PRIVATE);
        editor1.edit().clear().commit();

        passWord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int right=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX() >= passWord.getRight()-passWord.getCompoundDrawables()[right].getBounds().width()-40){
                        int selection= passWord.getSelectionEnd();
                        if(passWordVisible){
                            passWord.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_info_24,0,R.drawable.baseline_visibility_off_24,0);
                            passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passWordVisible=false;
                        }else{
                            passWord.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_info_24,0,R.drawable.baseline_visibility_24,0);
                            passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passWordVisible=true;
                        }
                        passWord.setSelection(selection);
                        return true;
                    }
                }

                return false;
            }
        });


//        if(mAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(), SecondActivity.class));
//
//        }



        mDatabase = FirebaseDatabase.getInstance().getReference();
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email= email.getText().toString().trim();
                String Pass= passWord.getText().toString().trim();
                String PhoneNumber= phoneNumber.getText().toString().trim();
                String FullName= fullName.getText().toString().trim();
                String Address= address.getText().toString().trim();

                if(TextUtils.isEmpty(Email)){
                    email.setError("Nhập Email");
                    return;
                }
                if(TextUtils.isEmpty(Pass)){
                    passWord.setError("Nhập mật khẩu");
                    return;
                }
                if(TextUtils.isEmpty(PhoneNumber)){
                    phoneNumber.setError("Nhập số điện thoại");
                    return;
                }
                if(TextUtils.isEmpty(FullName)){
                    fullName.setError("Nhập tên");
                    return;
                }
                if(TextUtils.isEmpty(Address)){
                    address.setError("Nhập địa chỉ");
                    return;
                }
                if(Pass.length() <6){
                    passWord.setError("Mật khẩu ít nhất 6 ký tự");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
//                Dk len firebasse
                mAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user= mAuth.getCurrentUser();
                            String uid=user.getUid().toString();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    writeNewUser(FullName,PhoneNumber,Address,Email,uid);
                                    showDialog();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivities.this,"Không gửi được email xác thực "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else{
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivities.this,"Lỗi "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });

    }

    private void anhxa(){
        fullName=findViewById(R.id.fullName);
        phoneNumber= findViewById(R.id.phoneNumber);
        address= findViewById(R.id.Address);
        email=findViewById(R.id.registerEmail);
        passWord=findViewById(R.id.password);
        registerbtn=findViewById(R.id.registerbtn);
        progressBar=findViewById(R.id.progressBar);
    }

    public void showDialog(){
        AlertDialog.Builder builder= new AlertDialog.Builder(RegisterActivities.this);
        builder.setTitle("Đăng ký thành công");
        builder.setMessage("Đã gửi email xác thực. Vui lòng vào địa chỉ email để xác thực tài khoản.Bạn sẽ không đăng nhập được nếu không xác thực tài khoản.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(), LoginActivities.class));
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    };


    public void writeNewUser(String fullName, String phoneNumber, String address, String email, String uid) {
        User user = new User(fullName, phoneNumber,address,email,uid);
        mDatabase.child("Users").child(uid).setValue(user);
    }

}
package com.example.shareapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareapp.Class.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView name,email, address;
    Button signOutBtn;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        address=findViewById(R.id.addressGet);
        signOutBtn = findViewById(R.id.signout);

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        reađataUser(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        gsc = GoogleSignIn.getClient(this,gso);

        SharedPreferences editor1= MainActivity.this.getSharedPreferences("data",MODE_PRIVATE);
        editor1.edit().clear().commit();

//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//        if(acct!=null){
//            String personName = acct.getDisplayName();
//            String personEmail = acct.getEmail();
//            name.setText(personName);
//            email.setText(personEmail);
//
//            SharedPreferences.Editor editor= getSharedPreferences("data",MODE_PRIVATE).edit();
//            editor.putString("email",personEmail);
//            editor.putString("pass",personName);
//            editor.putBoolean("isLogin",true);
//            editor.putString("type","google");
//
//            editor.apply();
//        }
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
//            name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            SharedPreferences.Editor editor= getSharedPreferences("data", Context.MODE_PRIVATE).edit();
            editor.putString("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
            editor.putBoolean("isLogin",true);
            editor.putString("type","EmailPassWord");
            editor.apply();
        }

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }


    public void reađataUser(String uid){
        mDatabase.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().exists()){
                            DataSnapshot dataSnapshot = task.getResult();
                            String fullName= String.valueOf(dataSnapshot.child("fullName").getValue());
                            String addressGet= String.valueOf(dataSnapshot.child("address").getValue());

                            name.setText(fullName);
                            address.setText(addressGet);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Không có người dùng này", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Không đọc được", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }
    void signOut(){
                FirebaseAuth.getInstance().signOut();
                SharedPreferences editor= MainActivity.this.getSharedPreferences("data",MODE_PRIVATE);
                editor.edit().clear().commit();

                SharedPreferences editor1= MainActivity.this.getSharedPreferences("dataPass",MODE_PRIVATE);
                editor1.edit().clear().commit();

                Intent i = new Intent(MainActivity.this,Login.class);
                startActivity(i);
                finish();
    }
}
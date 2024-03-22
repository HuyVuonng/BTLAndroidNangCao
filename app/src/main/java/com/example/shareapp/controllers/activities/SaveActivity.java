package com.example.shareapp.controllers.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shareapp.R;

import java.util.Date;

public class SaveActivity extends AppCompatActivity {
    EditText edtName, edtTime, edtLongitude, edtLatitude;
    Button btnLuu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        getViews();
        setEventListener();
    }

    private void setEventListener() {
        Intent intent = getIntent();
        double longitude = intent.getDoubleExtra("longitude", 0);
        double latitude = intent.getDoubleExtra("latitude", 0);
        edtLongitude.setText(String.valueOf(longitude));
        edtLatitude.setText(String.valueOf(latitude));
        edtTime.setText(new Date().toString());

        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("kiemtra", MODE_PRIVATE).edit();
                editor.putString("name", edtName.getText().toString());
                editor.putString("time", edtTime.getText().toString());
                editor.putString("longitude", edtLongitude.getText().toString());
                editor.putString("latitude", edtLatitude.getText().toString());
                editor.apply();
                Toast.makeText(SaveActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getViews() {
        edtName = findViewById(R.id.edt_ten);
        edtTime = findViewById(R.id.edt_time);
        edtLongitude = findViewById(R.id.edt_longitude);
        edtLatitude = findViewById(R.id.edt_latitude);
        btnLuu = findViewById(R.id.btn_luu);
    }
}
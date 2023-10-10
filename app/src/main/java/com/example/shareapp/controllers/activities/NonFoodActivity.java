package com.example.shareapp.controllers.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shareapp.R;
import com.example.shareapp.controllers.methods.NavigationMethod;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NonFoodActivity extends AppCompatActivity {
    BottomNavigationView bnv_menu;

    private void getViews() {
        this.bnv_menu = findViewById(R.id.main_bnv_menu);

    }

    private void setEventListener() {
        bnv_menu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.item_home) {
                Intent i = new Intent(NonFoodActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
            if (id == R.id.item_account) {
                Intent i = new Intent(NonFoodActivity.this, UserInforActivity.class);
                startActivity(i);
                finish();
            }
            if (id == R.id.item_search) {
                Intent i = new Intent(NonFoodActivity.this, SearchActivity.class);
                startActivity(i);
                finish();
            }
            return true;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getViews();

        this.setEventListener();

        NavigationMethod.setNavigationMenu(this.bnv_menu, R.id.item_non_food);

    }
}

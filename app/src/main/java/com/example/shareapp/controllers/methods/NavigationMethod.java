package com.example.shareapp.controllers.methods;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class NavigationMethod {
    public static void setNavigationMenu(BottomNavigationView bnv_menu, int selectedItemId) {
        //Bỏ màu nền
//        bnv_menu.setBackground(null);
//        bnv_menu.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        //Set item được chọn
        bnv_menu.setSelectedItemId(selectedItemId);
        MenuItem item = bnv_menu.getMenu().findItem(selectedItemId);
    }
}

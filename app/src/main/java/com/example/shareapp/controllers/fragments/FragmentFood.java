package com.example.shareapp.controllers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shareapp.R;

import java.util.List;

public class FragmentFood extends Fragment {
    private View mView;
    private ListView lvFood;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.fragment_food, container, false);

        getViews();
        setEventListener();

        return mView;
    }

    private void getViews() {
        lvFood = mView.findViewById(R.id.lv_food);
    }

    private void setEventListener() {
        // đổ dữ liệu post vào lsFood
    }
}

package com.example.shareapp.controllers.fragments;

import static com.example.shareapp.models.User.getUserInfor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shareapp.R;

public class FragmentNonFood extends Fragment {
    private View mView;
    private ListView lvNonFood;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.fragment_nonfood, container, false);

        getViews();
        setEventListener();
        return mView;
    }

    private void getViews() {
        lvNonFood = mView.findViewById(R.id.lv_non_food);
    }

    private void setEventListener() {
        // đổ dữ liệu post vào lsFood
    }
}

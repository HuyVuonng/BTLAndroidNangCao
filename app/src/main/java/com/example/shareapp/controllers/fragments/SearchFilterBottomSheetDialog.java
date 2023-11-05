package com.example.shareapp.controllers.fragments;

import static com.example.shareapp.controllers.activities.MainActivity.ACTION_CREATE_POST;
import static com.example.shareapp.controllers.activities.MainActivity.ACTION_NAME;
import static com.example.shareapp.controllers.activities.MainActivity.NAME_TYPE;
import static com.example.shareapp.controllers.activities.MainActivity.TYPE_FOOD;
import static com.example.shareapp.controllers.activities.MainActivity.TYPE_NON_FOOD;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.shareapp.R;
import com.example.shareapp.controllers.activities.CreatePostActivity;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFilterBottomSheetDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFilterBottomSheetDialog extends AppCompatDialogFragment {

    protected int type = 0;
    protected int location = 0;
    protected int sort = 0;




    Button btn_type_all,btn_type_food, btn_type_non_food,
            btn_location_all, btn_location_1, btn_location_3, btn_location_5, btn_location_10,
            btn_apply;

    RadioGroup rg_sort;

    View view;

    public SearchFilterBottomSheetDialog(int type, int location, int sort) {
        this.type = type;
        this.location = location;
        this.sort = sort;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        this.view = inflater.inflate(R.layout.bottom_sheet_search_filter, null);
        this.getViews(this.view);
        this.setTypeValue(this.type);
        this.setLocationValue(this.location);
        this.setSortValue(this.sort);
        this.setEventListener();
        builder.setView(this.view);
        return builder.create();
    }


    private void setEventListener() {
    }

    private void getViews(View view) {
        btn_type_all = view.findViewById(R.id.search_filter_btn_type_all);
        btn_type_food = view.findViewById(R.id.search_filter_btn_type_food);
        btn_type_non_food = view.findViewById(R.id.search_filter_btn_type_non_food);
        btn_location_all = view.findViewById(R.id.search_filter_btn_location_all);
        btn_location_1 = view.findViewById(R.id.search_filter_btn_location_1);
        btn_location_3 = view.findViewById(R.id.search_filter_btn_location_3);
        btn_location_5 = view.findViewById(R.id.search_filter_btn_location_5);
        btn_location_10 = view.findViewById(R.id.search_filter_btn_location_10);
        btn_apply = view.findViewById(R.id.search_filter_btn_apply);
        rg_sort = view.findViewById(R.id.search_filter_rg_sort);
    }

    private void setTypeValue(int type_id) {
        btn_type_all.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_type_food.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_type_non_food.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        this.view.findViewById(type_id).setBackgroundTintList(getResources().getColorStateList(R.color.purple_main));
    }

    private void setLocationValue(int location_id) {
        btn_location_all.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_location_1.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_location_3.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_location_5.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_location_10.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        this.view.findViewById(location_id).setBackgroundTintList(getResources().getColorStateList(R.color.purple_main));
    }

    private void setSortValue(int sort_id) {
        rg_sort.check(sort_id);
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
    }
}
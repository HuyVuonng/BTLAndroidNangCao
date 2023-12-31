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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.shareapp.R;
import com.example.shareapp.controllers.activities.CreatePostActivity;

import java.util.Objects;


public class PostAddSelectTypeBottomSheetDialog extends AppCompatDialogFragment {
    LinearLayout ll_food, ll_non_food;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.bottom_sheet_add_post, null);
        ll_non_food = view.findViewById(R.id.ll_add_post_select_type_non_food);
        ll_food = view.findViewById(R.id.ll_add_post_select_type_food);
        this.setEventListener();
        builder.setView(view);
        return builder.create();
    }


    private void setEventListener() {
        ll_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                intent.putExtra(ACTION_NAME, ACTION_CREATE_POST);
                intent.putExtra(NAME_TYPE, TYPE_FOOD);
                startActivity(intent);
                requireDialog().dismiss();
            }
        });
        ll_non_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                intent.putExtra(ACTION_NAME, ACTION_CREATE_POST);
                intent.putExtra(NAME_TYPE, TYPE_NON_FOOD);
                startActivity(intent);
                requireDialog().dismiss();
            }
        });
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

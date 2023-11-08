package com.example.shareapp.controllers.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.shareapp.R;

import java.util.List;
import java.util.Objects;

public class SearchFilterBottomSheetDialog extends AppCompatDialogFragment {

    protected int type = 0;
    protected int location = 0;
    protected int sort = 0;
    protected List<Integer> typeList;
    protected  List<Integer> userFilterKeyList;
    private DialogCallback callback;

    public static class Data {
        private int type;
        private int location;
        private int sort;
        private List<Integer> typeList;
        private List<Integer> userFilterKeyList;
        public Data(int type, int location, int sort,
                    List<Integer> typeList, List<Integer> userFilterKeyList) {
            this.location = location;
            this.sort = sort;
            this.type = type;
            this.typeList = typeList;
            this.userFilterKeyList = userFilterKeyList;
        }

        public int getType() { return this.type;}
        public int getLocation() { return this.location;}
        public int getSort() { return this.sort;}
        public List<Integer> getTypeList() { return this.typeList;}
        public List<Integer> getUserFilterKeyList() { return this.userFilterKeyList;}

     }

    public interface DialogCallback {
        void onDataReturned(Data data);
    }


    Button btn_user, btn_product,
            btn_type_all,btn_type_food, btn_type_non_food,
            btn_location_all, btn_location_1, btn_location_3, btn_location_5, btn_location_10,
            btn_user_email, btn_user_name, btn_user_description,
            btn_apply;

    RadioGroup rg_sort;

    LinearLayout ll_product_filter, ll_user_filter;
    View view;

    public SearchFilterBottomSheetDialog(
            int type,
            int location,
            int sort,
            List<Integer> typeList,
            List<Integer> userFilterKeyList,
            DialogCallback callback) {
        this.type = type;
        this.location = location;
        this.sort = sort;
        this.typeList = typeList;
        this.userFilterKeyList = userFilterKeyList;
        this.callback = callback;
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
        this.setSearchType(this.typeList);
        this.setUserKeyFilterValue(this.userFilterKeyList);
        this.setEventListener();
        builder.setView(this.view);
        return builder.create();
    }


    private void setEventListener() {
        this.btn_type_all.setOnClickListener(v -> this.setTypeValue(R.id.search_filter_btn_type_all));
        this.btn_type_food.setOnClickListener(v -> this.setTypeValue(R.id.search_filter_btn_type_food));
        this.btn_type_non_food.setOnClickListener(v -> this.setTypeValue(R.id.search_filter_btn_type_non_food));
        this.btn_location_all.setOnClickListener(v -> this.setLocationValue(R.id.search_filter_btn_location_all));
        this.btn_location_1.setOnClickListener(v -> this.setLocationValue(R.id.search_filter_btn_location_1));
        this.btn_location_3.setOnClickListener(v -> this.setLocationValue(R.id.search_filter_btn_location_3));
        this.btn_location_5.setOnClickListener(v -> this.setLocationValue(R.id.search_filter_btn_location_5));
        this.btn_location_10.setOnClickListener(v -> this.setLocationValue(R.id.search_filter_btn_location_10));
        this.btn_user.setOnClickListener(v -> {
            int index = this.typeList.indexOf(R.id.search_filter_btn_user);
            if (index != -1) {
                this.typeList.remove(index);
                if (this.typeList.size() ==0 ) this.typeList.add(R.id.search_filter_btn_product);
            } else {
                this.typeList.add(R.id.search_filter_btn_user);
            }
            this.setSearchType(this.typeList);
        });
        this.btn_product.setOnClickListener(v -> {
            int index = this.typeList.indexOf(R.id.search_filter_btn_product);
            if (index != -1) {
                this.typeList.remove(index);
                if (this.typeList.size() ==0 ) this.typeList.add(R.id.search_filter_btn_user);
            } else {
                this.typeList.add(R.id.search_filter_btn_product);
            }
            this.setSearchType(this.typeList);
        });
        this.btn_user_email.setOnClickListener(v -> {
            int index = this.userFilterKeyList.indexOf(R.id.search_filter_btn_user_email);
            if (index != -1) {
                this.userFilterKeyList.remove(index);
                if (this.userFilterKeyList.size() ==0 ) {
                    this.userFilterKeyList.add(R.id.search_filter_btn_user_name);
                    this.userFilterKeyList.add(R.id.search_filter_btn_user_description);
                    this.userFilterKeyList.add(R.id.search_filter_btn_user_email);
                }
            } else {
                this.userFilterKeyList.add(R.id.search_filter_btn_user_email);
            }
            this.setUserKeyFilterValue(this.userFilterKeyList);
        });
        this.btn_user_name.setOnClickListener(v -> {
            int index = this.userFilterKeyList.indexOf(R.id.search_filter_btn_user_name);
            if (index != -1) {
                this.userFilterKeyList.remove(index);
                if (this.userFilterKeyList.size() ==0 ) {
                    this.userFilterKeyList.add(R.id.search_filter_btn_user_name);
                    this.userFilterKeyList.add(R.id.search_filter_btn_user_description);
                    this.userFilterKeyList.add(R.id.search_filter_btn_user_email);
                }
            } else {
                this.userFilterKeyList.add(R.id.search_filter_btn_user_name);
            }
            this.setUserKeyFilterValue(this.userFilterKeyList);
        });
        this.btn_user_description.setOnClickListener(v -> {
            int index = this.userFilterKeyList.indexOf(R.id.search_filter_btn_user_description);
            if (index != -1) {
                this.userFilterKeyList.remove(index);
                if (this.userFilterKeyList.size() ==0 ) {
                    this.userFilterKeyList.add(R.id.search_filter_btn_user_name);
                    this.userFilterKeyList.add(R.id.search_filter_btn_user_description);
                    this.userFilterKeyList.add(R.id.search_filter_btn_user_email);
                }
            } else {
                this.userFilterKeyList.add(R.id.search_filter_btn_user_description);
            }
            this.setUserKeyFilterValue(this.userFilterKeyList);
        });
        this.rg_sort.setOnCheckedChangeListener((group, checkedId) -> {
            this.sort = checkedId;
        });
        this.btn_apply.setOnClickListener(v -> {
            callback.onDataReturned(new Data(this.type, this.location, this.sort,
                    this.typeList, this.userFilterKeyList));
            dismiss();
        });
    }

    private void getViews(View view) {
        btn_user = view.findViewById(R.id.search_filter_btn_user);
        btn_product = view.findViewById(R.id.search_filter_btn_product);
        btn_type_all = view.findViewById(R.id.search_filter_btn_type_all);
        btn_type_food = view.findViewById(R.id.search_filter_btn_type_food);
        btn_type_non_food = view.findViewById(R.id.search_filter_btn_type_non_food);
        btn_location_all = view.findViewById(R.id.search_filter_btn_location_all);
        btn_location_1 = view.findViewById(R.id.search_filter_btn_location_1);
        btn_location_3 = view.findViewById(R.id.search_filter_btn_location_3);
        btn_location_5 = view.findViewById(R.id.search_filter_btn_location_5);
        btn_location_10 = view.findViewById(R.id.search_filter_btn_location_10);
        btn_user_email = view.findViewById(R.id.search_filter_btn_user_email);
        btn_user_name = view.findViewById(R.id.search_filter_btn_user_name);
        btn_user_description = view.findViewById(R.id.search_filter_btn_user_description);
        btn_apply = view.findViewById(R.id.search_filter_btn_apply);
        rg_sort = view.findViewById(R.id.search_filter_rg_sort);
        ll_product_filter = view.findViewById(R.id.search_filter_ll_product_filter);
        ll_user_filter = view.findViewById(R.id.search_filter_ll_user_filter);
    }

    private void setSearchType(List<Integer> typeList) {
        btn_user.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_product.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        ll_product_filter.setVisibility(View.GONE);
        ll_user_filter.setVisibility(View.GONE);
        for (Integer type: typeList) {
            this.view.findViewById(type).setBackgroundTintList(getResources().getColorStateList(R.color.purple_main));
            if (type == R.id.search_filter_btn_user) ll_user_filter.setVisibility(View.VISIBLE);
            if (type == R.id.search_filter_btn_product) ll_product_filter.setVisibility(View.VISIBLE);
        }
    }

    private void setTypeValue(int type_id) {
        btn_type_all.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_type_food.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_type_non_food.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        this.view.findViewById(type_id).setBackgroundTintList(getResources().getColorStateList(R.color.purple_main));
        this.type = type_id;
    }

    private void setLocationValue(int location_id) {
        btn_location_all.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_location_1.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_location_3.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_location_5.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_location_10.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        this.view.findViewById(location_id).setBackgroundTintList(getResources().getColorStateList(R.color.purple_main));
        this.location = location_id;
    }

    private void setUserKeyFilterValue(List<Integer> keys) {
        btn_user_email.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_user_name.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        btn_user_description.setBackgroundTintList(getResources().getColorStateList(R.color.grey_main));
        for (Integer key: keys) {
            this.view.findViewById(key).setBackgroundTintList(getResources().getColorStateList(R.color.purple_main));
        }
    }

    private void setSortValue(int sort_id) {
        rg_sort.check(sort_id);
        this.sort = sort_id;
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
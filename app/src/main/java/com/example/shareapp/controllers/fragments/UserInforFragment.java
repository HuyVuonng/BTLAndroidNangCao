package com.example.shareapp.controllers.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.shareapp.models.User.getUserInfor;
import static com.example.shareapp.models.User.readDataUserFromFireBase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shareapp.R;
import com.example.shareapp.controllers.activities.LoginActivity;
import com.example.shareapp.controllers.activities.UserInforUpdateActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInforFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInforFragment extends Fragment {
    public UserInforFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private DatabaseReference mDatabase;
    BottomNavigationView bnv_menu;
    FloatingActionButton btn_add_post;


    TextView fullNameView, emailView, phoneNumberView, addressView, logoutBTN;
    Button EditInforBTN;
    ProgressBar progressBar;
    ImageView avataView;
    GoogleSignInClient gsc;
    GoogleSignInOptions gso;
    private  View mVIew;

    private void getViews() {
        fullNameView = mVIew.findViewById(R.id.fullNameView);
        emailView = mVIew.findViewById(R.id.emailView);
        phoneNumberView = mVIew.findViewById(R.id.phoneNumberView);
        addressView = mVIew.findViewById(R.id.addressView);
        progressBar = mVIew.findViewById(R.id.progressBar4);
        EditInforBTN = mVIew.findViewById(R.id.EditInforbtn);
        logoutBTN = mVIew.findViewById(R.id.activity_userInfor_tv_logout);
        avataView =  mVIew.findViewById(R.id.activity_userInfor_imgv_avata);
    }
    private void setEventListener() {
//        bnv_menu.setOnItemSelectedListener(item -> {
//            int id = item.getItemId();
//            if (id == R.id.item_home) {
//                Intent i = new Intent(getContext(), MainActivity.class);
//                startActivity(i);
//                finish();
//            }
//            if (id == R.id.item_non_food) {
//                Intent i = new Intent(getContext(), NonFoodActivity.class);
//                startActivity(i);
//                finish();
//            }
//            if (id == R.id.item_search) {
//                Intent i = new Intent(getContext(), SearchActivity.class);
//                startActivity(i);
//                finish();
//            }
//            return true;
//        });


        EditInforBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), UserInforUpdateActivity.class));
            }
        });
        logoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVIew =  inflater.inflate(R.layout.fragment_user_infor,container,false);

        readDataUserFromFireBase(getUserInfor(getActivity()).uid, getActivity());
        this.getViews();
        this.setEventListener();
        setDataToView();
        return mVIew;
    }
    public void setDataToView() {
        fullNameView.setText(getUserInfor(getActivity()).getFullName().toString());
        addressView.setText(getUserInfor(getActivity()).getAddress().toString());
        emailView.setText(getUserInfor(getActivity()).getEmail().toString());
        phoneNumberView.setText(getUserInfor(getActivity()).getPhoneNumber().toString());
        if (getUserInfor(getActivity()).getAvata() != "") {
            Glide.with(getActivity()).load(getUserInfor(getActivity()).getAvata().toString()).into(avataView);
        }
    }

    public void LogOut() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getActivity(), gso);
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                SharedPreferences editor = getActivity().getSharedPreferences("data", MODE_PRIVATE);
                editor.edit().clear().apply();

                SharedPreferences editor1 = getActivity().getSharedPreferences("dataPass", MODE_PRIVATE);
                editor1.edit().clear().apply();

                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                FirebaseAuth.getInstance().signOut();

            }
        });
    }
}
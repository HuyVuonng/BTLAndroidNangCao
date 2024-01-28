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
import com.example.shareapp.models.Post;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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


    TextView fullNameView, emailView, phoneNumberView, addressView, introduceView, numberOfPostView, tvTotalPrice;

    ProgressBar progressBar;
    ImageView avataView;
    GoogleSignInClient gsc;
    GoogleSignInOptions gso;
    private View mVIew;
    int soBaiDang = 0;
    private long totalPrice = 0;

    private void getViews() {
        fullNameView = mVIew.findViewById(R.id.fullNameView);
        emailView = mVIew.findViewById(R.id.emailView);
        phoneNumberView = mVIew.findViewById(R.id.phoneNumberView);
        addressView = mVIew.findViewById(R.id.addressView);
        progressBar = mVIew.findViewById(R.id.progressBar4);
        avataView = mVIew.findViewById(R.id.activity_userInfor_imgv_avata);
        introduceView = mVIew.findViewById(R.id.IntroduceView);
        numberOfPostView = mVIew.findViewById(R.id.numberOfPostView);
        tvTotalPrice = mVIew.findViewById(R.id.tv_total_price);
    }

    private void getNumberOfPost(String uid) {
        Post.getFirebaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getUserId().equals(uid)) {
                        totalPrice += post.getPrice() * post.getCount();
                        soBaiDang++;
                    }
                }
                numberOfPostView.setText("Tổng số bài đăng: " + soBaiDang);
                tvTotalPrice.setText("Số tiền bạn đã cho đi: " + totalPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVIew = inflater.inflate(R.layout.fragment_user_infor, container, false);

        readDataUserFromFireBase(getUserInfor(getActivity()).uid, getActivity());
        this.getViews();
        this.setDataToView();
        return mVIew;
    }

    public void setDataToView() {
        fullNameView.setText(getUserInfor(getActivity()).getFullName().toString());
        addressView.setText(getUserInfor(getActivity()).getAddress().toString());
        emailView.setText(getUserInfor(getActivity()).getEmail().toString());
        phoneNumberView.setText(getUserInfor(getActivity()).getPhoneNumber().toString());
        if (getUserInfor(getActivity()).getAvata().length() > 0) {
            Glide.with(getActivity()).load(getUserInfor(getActivity()).getAvata().toString()).into(avataView);
        }
        introduceView.setText(getUserInfor(getActivity()).getIntroduce().toString());
        getNumberOfPost(getUserInfor(getActivity()).getUid().toString());
    }
}
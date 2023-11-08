package com.example.shareapp.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shareapp.R;
import com.example.shareapp.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> listUser;
    private View view;

    public UserAdapter(Context _context, List<User> listUser) {
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

        return new UserViewHolder(this.view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = this.listUser.get(position);
        if(user == null)
            return;
        holder.tv_username.setText(user.getFullName());
        holder.tv_email.setText(user.getEmail());
        Glide.with(this.view).load(user.getAvata()).into(holder.civ_avatar);
        holder.btn_detail.setOnClickListener(v -> {
            Toast.makeText(this.view.getContext(), "Todo: Navigate to User Detail", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return this.listUser.size();
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private CardView cv_item;
        private CircleImageView civ_avatar;
        private TextView tv_username, tv_email;
        private Button btn_detail;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cv_item = itemView.findViewById(R.id.user_cv_item);
            this.civ_avatar = itemView.findViewById(R.id.item_user_civ_avatar);
            this.tv_username = itemView.findViewById(R.id.item_user_tv_username);
            this.tv_email = itemView.findViewById(R.id.item_user_tv_email);
            this.btn_detail = itemView.findViewById(R.id.item_user_btn_detail);
        }
    }

}
